package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LessonService implements LessonUseCase {

    private final LessonRepository lessonRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper mapper;
    private final LessonMapper lessonMapper;

    private static final String lessonNotFoundMessage = "Lesson não encontrado";

    @Override
    public GenericModelResponse createLesson(CreateLessonModelRequest request) {
        Lesson lesson = lessonMapper.requestToEntity(request);
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("LESSON_CREATED", "Lesson created successfully!");
    }

    @Override
    public GenericModelResponse deleteLesson(String request) {
        UUID lessonId = UUID.fromString(request);
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        lessonRepository.deleteById(lessonId);

        return new GenericModelResponse("LESSON_DELETED", "Lesson deleted successfully!");
    }

    @Override
    public LessonModelResponse listLessonById(String request) {
        UUID lessonId = UUID.fromString(request);
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        return lessonMapper.entityToResponse(lesson);
    }

    @Override
    public GenericModelResponse updateLesson(String lessonId, UpdatelessonModelRequest request) {
        UUID lessonIdParsed = UUID.fromString(lessonId);
        var lesson = lessonMapper.requestToEntity(request, lessonIdParsed);
        lessonRepository.findById(lessonIdParsed)
                .orElseThrow(() -> new NotFoundException(lessonNotFoundMessage));
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("LESSON_UPDATED", "Lesson updated successfully!");
    }

    @Override
    public List<LessonModelResponse> listLessonByMentorId(UUID request) {
        var lessons = lessonRepository.findByMentorId(request);
        return lessonMapper.entityToResponse(lessons);
    }

    @Override
    public List<LessonModelResponse> listLesson(String title, LocalDate date, String order) {
        Specification<Lesson> spec = (root, query, cb) -> cb.conjunction();

        if (title != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (date != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("DATE", LocalDate.class, root.get("startTime")), date)
            );
        }
        Specification<Lesson> orderBySpec = (root, query, cb) -> {
            if ("desc".equalsIgnoreCase(order)) {
                query.orderBy(cb.desc(root.get("startTime")));
            } else {
                query.orderBy(cb.asc(root.get("startTime")));
            }
            return null;
        };

        spec = spec.and(orderBySpec);

        var lessons = lessonRepository.findAll(spec);
        return lessonMapper.entityToResponse(lessons);
    }
}
