package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.*;
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

    @Override
    public GenericModelResponse createlesson(CreatelessonModelRequest request) {
        var lesson = lessonMapper.requestToEntity(request);
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("lesson_CREATED", "lesson created successfully!");
    }

    @Override
    public GenericModelResponse deletelesson(String request) {
        UUID lessonId = UUID.fromString(request);
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("lesson não encontrado"));
        lessonRepository.deleteById(lessonId);

        return new GenericModelResponse("lesson_DELETED", "lesson deleted successfully!");
    }

    @Override
    public LessonModelResponse listlessonById(String request) {
        UUID lessonId = UUID.fromString(request);
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("lesson não encontrado"));
        return lessonMapper.entityToResponse(lesson);
    }

    @Override
    public GenericModelResponse updatelesson(String lessonId, UpdatelessonModelRequest request) {
        UUID lessonIdParsed = UUID.fromString(lessonId);
        var lesson = lessonMapper.requestToEntity(request, lessonIdParsed);
        lessonRepository.findById(lessonIdParsed)
                .orElseThrow(() -> new NotFoundException("lesson não encontrado"));
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("lesson_UPDATED", "lesson updated successfully!");
    }

    @Override
    public List<LessonModelResponse> listLeasonByMentorId(UUID request) {
        var lessons = lessonRepository.findByMentorId(request);
        return lessonMapper.entityToResponse(lessons);
    }

    @Override
    public List<LessonModelResponse> listLesson(String title, LocalDate date) {
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

        var lessons = lessonRepository.findAll(spec);
        return lessonMapper.entityToResponse(lessons);
    }
}
