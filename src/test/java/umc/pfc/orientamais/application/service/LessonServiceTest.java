package umc.pfc.orientamais.application.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonMentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.LessonRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.Lesson;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    private LessonRepository lessonRepository;
    private MentorRepository mentorRepository;
    private ModelMapper mapper;
    private LessonMapper lessonMapper;
    private LessonService lessonService;
    private MentoredRepository mentoredRepository;
    private LessonMentoredRepository lessonMentoredRepository;

    @BeforeEach
    void setUp() {
        lessonRepository = mock(LessonRepository.class);
        mentorRepository = mock(MentorRepository.class);
        mapper = mock(ModelMapper.class);
        lessonMapper = mock(LessonMapper.class);
        lessonService = new LessonService(
                lessonRepository,
                mentorRepository,
                mentoredRepository,
                lessonMentoredRepository,
                mapper,
                lessonMapper
        );
    }

    @Test
    void shouldCreateLessonSuccessfully() {
        var mentorId = UUID.randomUUID();
        var request = new CreateLessonModelRequest();
        request.setTitle("Aula 1");
        request.setDescription("Descrição");
        request.setLink("link.com");
        request.setMaxGuest(10);
        request.setDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(11, 0));
        request.setMentorId(mentorId);

        var lesson = new Lesson();
        when(lessonMapper.requestToEntity(request)).thenReturn(lesson);
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));

        GenericModelResponse response = lessonService.createLesson(request);

        assertEquals("LESSON_CREATED", response.getCode());
        assertEquals("Lesson created successfully!", response.getMessage());
        verify(lessonRepository).save(lesson);
    }

    @Test
    void shouldThrowWhenMentorNotFoundOnCreate() {
        var request = new CreateLessonModelRequest();
        request.setMentorId(UUID.randomUUID());
        when(lessonMapper.requestToEntity(request)).thenReturn(new Lesson());
        when(mentorRepository.findById(request.getMentorId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lessonService.createLesson(request));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void shouldDeleteLessonSuccessfully() {
        var id = UUID.randomUUID();
        var lesson = new Lesson();
        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));

        GenericModelResponse response = lessonService.deleteLesson(id.toString());

        assertEquals("LESSON_DELETED", response.getCode());
        assertEquals("Lesson deleted successfully!", response.getMessage());
        verify(lessonRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenLessonNotFoundOnDelete() {
        var id = UUID.randomUUID();
        when(lessonRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lessonService.deleteLesson(id.toString()));
    }

    @Test
    void shouldListLessonByIdSuccessfully() {
        var id = UUID.randomUUID();
        var lesson = new Lesson();
        var expectedResponse = new LessonModelResponse();
        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));
        when(lessonMapper.entityToResponse(lesson)).thenReturn(expectedResponse);

        LessonModelResponse response = lessonService.listLessonById(id.toString());

        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldThrowWhenLessonNotFoundOnListById() {
        var id = UUID.randomUUID();
        when(lessonRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lessonService.listLessonById(id.toString()));
    }

    @Test
    void shouldUpdateLessonSuccessfully() {
        var id = UUID.randomUUID();
        var request = new UpdatelessonModelRequest();
        request.setMentorId(UUID.randomUUID());
        var lesson = new Lesson();
        when(lessonMapper.requestToEntity(request, id)).thenReturn(lesson);
        when(lessonRepository.findById(id)).thenReturn(Optional.of(new Lesson()));
        when(mentorRepository.findById(request.getMentorId())).thenReturn(Optional.of(new Mentor()));

        GenericModelResponse response = lessonService.updateLesson(id.toString(), request);

        assertEquals("LESSON_UPDATED", response.getCode());
        assertEquals("Lesson updated successfully!", response.getMessage());
        verify(lessonRepository).save(lesson);
    }

    @Test
    void shouldThrowWhenLessonNotFoundOnUpdate() {
        var id = UUID.randomUUID();
        var request = new UpdatelessonModelRequest();
        request.setMentorId(UUID.randomUUID());
        when(lessonRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lessonService.updateLesson(id.toString(), request));
    }

    @Test
    void shouldThrowWhenMentorNotFoundOnUpdate() {
        var id = UUID.randomUUID();
        var request = new UpdatelessonModelRequest();
        request.setMentorId(UUID.randomUUID());
        when(lessonRepository.findById(id)).thenReturn(Optional.of(new Lesson()));
        when(mentorRepository.findById(request.getMentorId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lessonService.updateLesson(id.toString(), request));
    }

    @Test
    void shouldListLessonsByMentorIdSuccessfully() {
        var mentorId = UUID.randomUUID();
        var lessons = List.of(new Lesson());
        var responses = List.of(new LessonModelResponse());
        when(lessonRepository.findByMentorId(mentorId)).thenReturn(lessons);
        when(lessonMapper.entityToResponse(lessons)).thenReturn(responses);

        List<LessonModelResponse> result = lessonService.listLessonByMentorId(mentorId);

        assertEquals(responses, result);
    }
}
