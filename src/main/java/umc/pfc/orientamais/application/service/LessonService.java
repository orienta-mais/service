package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.*;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.Lesson;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LessonService implements LessonUseCase {

    private final LessonRepository lessonRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper mapper;

    @Override
    public GenericModelResponse createlesson(CreatelessonModelRequest request) {
        var lesson = mapper.map(request, Lesson.class);
        mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        lessonRepository.save(lesson);

        return new GenericModelResponse("lesson_CREATED", "lesson created successfully!");
    }

    @Override
    public GenericModelResponse deletelesson(DeletelessonModelRequest request) {
        UUID lessonId = UUID.fromString(request.getLessonId());
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
        return mapper.map(lesson, LessonModelResponse.class);
    }

    @Override
    public GenericModelResponse updatelesson(String lessonId, UpdatelessonModelRequest request) {
        UUID lessonIdParsed = UUID.fromString(lessonId);
        var lesson = mapper.map(request, Lesson.class);
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
        return lessons.stream()
                .map(lesson -> mapper.map(lesson, LessonModelResponse.class))
                .toList();
    }
}
