package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.CreateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdatelessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.application.port.input.LessonUseCase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonTest {

    @InjectMocks
    private Lesson lessonController;

    @Mock
    private LessonUseCase lessonUseCase;

    @Mock
    private ModelMapper mapper;

    private CreateLessonModelRequest createRequest;
    private UpdatelessonModelRequest updateRequest;
    private GenericModelResponse successResponse;
    private LessonModelResponse lessonModelResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateLessonModelRequest();
        createRequest.setTitle("Intro to Java");
        createRequest.setDescription("Basic syntax and core concepts");
        createRequest.setMaxGuest(10);
        createRequest.setDate(LocalDate.of(2025, 10, 20));
        createRequest.setStartTime(LocalTime.of(14, 0));
        createRequest.setEndTime(LocalTime.of(16, 0));
        createRequest.setMentorId(UUID.randomUUID());
        createRequest.setPresentCode("JAVA101");

        updateRequest = new UpdatelessonModelRequest();
        updateRequest.setTitle("Advanced Java");
        updateRequest.setDescription("Deep dive into Streams and Lambdas");
        updateRequest.setMaxGuest(20);
        updateRequest.setDate(LocalDate.of(2025, 10, 21));
        updateRequest.setStartTime(LocalTime.of(15, 0));
        updateRequest.setEndTime(LocalTime.of(17, 0));
        updateRequest.setMentorId(UUID.randomUUID());
        updateRequest.setPresentCode("JAVA202");

        successResponse = new GenericModelResponse("SUCCESS", "Operation completed successfully");

        lessonModelResponse = new LessonModelResponse();
        lessonModelResponse.setId(UUID.randomUUID());
        lessonModelResponse.setTitle("Intro to Java");
        lessonModelResponse.setDescription("Basic syntax and core concepts");
        lessonModelResponse.setLink("https://example.com/java-intro");
        lessonModelResponse.setMaxGuest(10);
        lessonModelResponse.setDate(LocalDate.of(2025, 10, 20));
        lessonModelResponse.setStartTime(LocalTime.of(14, 0));
        lessonModelResponse.setEndTime(LocalTime.of(16, 0));
        lessonModelResponse.setMentorName("John Mentor");
    }

    @Test
    void shouldCreateLessonSuccessfully() {
        when(lessonUseCase.createLesson(createRequest)).thenReturn(successResponse);

        ResponseEntity<GenericModelResponse> response = lessonController.createLesson(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getCode());
        assertEquals("Operation completed successfully", response.getBody().getMessage());
        verify(lessonUseCase).createLesson(createRequest);
    }

    @Test
    void shouldListLessonByIdSuccessfully() {
        when(lessonUseCase.listLessonById("1")).thenReturn(lessonModelResponse);

        ResponseEntity<?> response = lessonController.listLessonById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lessonModelResponse, response.getBody());
        verify(lessonUseCase).listLessonById("1");
    }

    @Test
    void shouldUpdateLessonSuccessfully() {
        when(lessonUseCase.updateLesson("1", updateRequest)).thenReturn(successResponse);

        ResponseEntity<GenericModelResponse> response = lessonController.updateLesson("1", updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getCode());
        verify(lessonUseCase).updateLesson("1", updateRequest);
    }

    @Test
    void shouldDeleteLessonSuccessfully() {
        when(lessonUseCase.deleteLesson("1")).thenReturn(successResponse);

        ResponseEntity<GenericModelResponse> response = lessonController.deleteLesson("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getCode());
        verify(lessonUseCase).deleteLesson("1");
    }

    @Test
    void shouldListLessonsByMentorSuccessfully() {
        UUID mentorId = UUID.randomUUID();
        when(lessonUseCase.listLessonByMentorId(mentorId)).thenReturn(List.of(lessonModelResponse));

        ResponseEntity<?> response = lessonController.listLessonById(mentorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(lessonModelResponse), response.getBody());
        verify(lessonUseCase).listLessonByMentorId(mentorId);
    }

    @Test
    void shouldReturnEmptyListWhenMentorHasNoLessons() {
        UUID mentorId = UUID.randomUUID();
        when(lessonUseCase.listLessonByMentorId(mentorId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = lessonController.listLessonById(mentorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void shouldListAllLessonsSuccessfully() {
        when(lessonUseCase.listLesson("Java", LocalDate.now(), "asc"))
                .thenReturn(List.of(lessonModelResponse));

        ResponseEntity<?> response = lessonController.listLesson("Java", LocalDate.now(), "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(lessonModelResponse), response.getBody());
        verify(lessonUseCase).listLesson("Java", LocalDate.now(), "asc");
    }

    @Test
    void shouldReturnEmptyListWhenNoLessonsFound() {
        when(lessonUseCase.listLesson("Kotlin", LocalDate.now(), "asc"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = lessonController.listLesson("Kotlin", LocalDate.now(), "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }
}
