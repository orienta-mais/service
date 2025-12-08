package umc.pfc.orientamais.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import umc.pfc.orientamais.adapters.input.rest.dto.request.PresenceCodeModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UpdateLessonModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountLessonsResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonDetailsModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LessonModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.PagedModelResponse;
import umc.pfc.orientamais.application.port.input.CertificateUseCase;
import umc.pfc.orientamais.application.port.input.LessonUseCase;
import umc.pfc.orientamais.application.port.input.MentorReviewUseCase;

@ExtendWith(MockitoExtension.class)
class LessonTest {

  @InjectMocks private Lesson lessonController;

  @Mock private LessonUseCase lessonUseCase;

  @Mock private CertificateUseCase certificateUseCase;

  @Mock private MentorReviewUseCase mentorReviewUseCase;

  @Mock private ModelMapper mapper;

  private CreateLessonModelRequest createRequest;
  private UpdateLessonModelRequest updateRequest;
  private PresenceCodeModelRequest presenceRequest;
  private GenericModelResponse successResponse;
  private GenericModelResponse certificateCreatedResponse;
  private LessonDetailsModelResponse lessonDetailsModelResponse;
  private LessonModelResponse lessonModelResponse;
  private PagedModelResponse<LessonModelResponse> pagedResponse;
  private CountLessonsResponse countLessonsResponse;
  private UUID mentorId;
  private UUID mentoredId;
  private UUID lessonUuid;

  @BeforeEach
  void setUp() {
    mentorId = UUID.randomUUID();
    mentoredId = UUID.randomUUID();
    lessonUuid = UUID.randomUUID();

    createRequest = new CreateLessonModelRequest();
    createRequest.setTitle("Intro to Java");
    createRequest.setDescription("Basic syntax and core concepts");
    createRequest.setMaxGuest(10);
    createRequest.setDate(LocalDate.of(2025, 10, 20));
    createRequest.setStartTime(LocalTime.of(14, 0));
    createRequest.setEndTime(LocalTime.of(16, 0));
    createRequest.setMentorId(UUID.randomUUID());
    createRequest.setPresentCode("JAVA101");

    updateRequest = new UpdateLessonModelRequest();
    updateRequest.setTitle("Advanced Java");
    updateRequest.setDescription("Deep dive into Streams and Lambdas");
    updateRequest.setMaxGuest(20);
    updateRequest.setDate(LocalDate.of(2025, 10, 21));
    updateRequest.setStartTime(LocalTime.of(15, 0));
    updateRequest.setEndTime(LocalTime.of(17, 0));
    updateRequest.setMentorId(UUID.randomUUID());
    updateRequest.setPresentCode("JAVA202");

    presenceRequest = new PresenceCodeModelRequest("CODE123");

    successResponse = new GenericModelResponse("SUCCESS", "Operation completed successfully");
    certificateCreatedResponse = new GenericModelResponse("SUCCESS", "Certificate generated");

    lessonDetailsModelResponse = new LessonDetailsModelResponse();
    lessonDetailsModelResponse.setId(UUID.randomUUID());
    lessonDetailsModelResponse.setTitle("Intro to Java");
    lessonDetailsModelResponse.setDescription("Basic syntax and core concepts");
    lessonDetailsModelResponse.setLink("https://example.com/java-intro");
    lessonDetailsModelResponse.setMaxGuest(10);
    lessonDetailsModelResponse.setDate(LocalDate.of(2025, 10, 20));
    lessonDetailsModelResponse.setStartTime(LocalTime.of(14, 0));
    lessonDetailsModelResponse.setEndTime(LocalTime.of(16, 0));
    lessonDetailsModelResponse.setMentorName("John Mentor");

    lessonModelResponse = new LessonModelResponse();

    pagedResponse = new PagedModelResponse<>();
    pagedResponse.setContent(List.of(lessonModelResponse));
    pagedResponse.setSize(10);
    pagedResponse.setTotal(1);
    pagedResponse.setTotalPages(1);
    pagedResponse.setCurrentPage(0);

    countLessonsResponse = new CountLessonsResponse();
    countLessonsResponse.setCountUpcomingLessons(3);
    countLessonsResponse.setCountUnavailableLessons(1);
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
  void shouldPropagateExceptionWhenCreateLessonFails() {
    when(lessonUseCase.createLesson(createRequest)).thenThrow(new IllegalStateException("error"));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> lessonController.createLesson(createRequest));

    assertEquals("error", exception.getMessage());
    verify(lessonUseCase).createLesson(createRequest);
  }

  @Test
  void shouldListLessonByIdSuccessfully() {
    when(lessonUseCase.listLessonById("1")).thenReturn(lessonDetailsModelResponse);

    ResponseEntity<LessonDetailsModelResponse> response = lessonController.listLessonById("1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(lessonDetailsModelResponse, response.getBody());
    verify(lessonUseCase).listLessonById("1");
  }

  @Test
  void shouldPropagateExceptionWhenListLessonByIdFails() {
    when(lessonUseCase.listLessonById("1")).thenThrow(new RuntimeException("not found"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> lessonController.listLessonById("1"));

    assertEquals("not found", exception.getMessage());
    verify(lessonUseCase).listLessonById("1");
  }

  @Test
  void shouldUpdateLessonSuccessfully() {
    when(lessonUseCase.updateLesson("1", updateRequest)).thenReturn(successResponse);

    ResponseEntity<GenericModelResponse> response =
        lessonController.updateLesson("1", updateRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("SUCCESS", response.getBody().getCode());
    verify(lessonUseCase).updateLesson("1", updateRequest);
  }

  @Test
  void shouldPropagateExceptionWhenUpdateLessonFails() {
    when(lessonUseCase.updateLesson("1", updateRequest))
        .thenThrow(new IllegalArgumentException("invalid"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> lessonController.updateLesson("1", updateRequest));

    assertEquals("invalid", exception.getMessage());
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
  void shouldPropagateExceptionWhenDeleteLessonFails() {
    when(lessonUseCase.deleteLesson("1")).thenThrow(new RuntimeException("delete error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> lessonController.deleteLesson("1"));

    assertEquals("delete error", exception.getMessage());
    verify(lessonUseCase).deleteLesson("1");
  }

  @Test
  void shouldListLessonsByMentorSuccessfully() {
    when(lessonUseCase.listLessonByMentorId(mentorId))
        .thenReturn(List.of(lessonDetailsModelResponse));

    ResponseEntity<List<LessonDetailsModelResponse>> response =
        lessonController.listLessonById(mentorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    verify(lessonUseCase).listLessonByMentorId(mentorId);
  }

  @Test
  void shouldReturnEmptyListWhenMentorHasNoLessons() {
    when(lessonUseCase.listLessonByMentorId(mentorId)).thenReturn(Collections.emptyList());

    ResponseEntity<List<LessonDetailsModelResponse>> response =
        lessonController.listLessonById(mentorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
    verify(lessonUseCase).listLessonByMentorId(mentorId);
  }

  @Test
  void shouldPropagateExceptionWhenListLessonsByMentorFails() {
    when(lessonUseCase.listLessonByMentorId(mentorId))
        .thenThrow(new RuntimeException("mentor error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> lessonController.listLessonById(mentorId));

    assertEquals("mentor error", exception.getMessage());
    verify(lessonUseCase).listLessonByMentorId(mentorId);
  }

  @Test
  void shouldListLessonsByMentoredSuccessfully() {
    when(lessonUseCase.listLessonByMentoredId(mentoredId))
        .thenReturn(List.of(lessonDetailsModelResponse));

    ResponseEntity<List<LessonDetailsModelResponse>> response =
        lessonController.listLessonByMentoredId(mentoredId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(List.of(lessonDetailsModelResponse), response.getBody());
    verify(lessonUseCase).listLessonByMentoredId(mentoredId);
  }

  @Test
  void shouldReturnEmptyListWhenMentoredHasNoLessons() {
    when(lessonUseCase.listLessonByMentoredId(mentoredId)).thenReturn(Collections.emptyList());

    ResponseEntity<List<LessonDetailsModelResponse>> response =
        lessonController.listLessonByMentoredId(mentoredId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
    verify(lessonUseCase).listLessonByMentoredId(mentoredId);
  }

  @Test
  void shouldPropagateExceptionWhenListLessonsByMentoredFails() {
    when(lessonUseCase.listLessonByMentoredId(mentoredId))
        .thenThrow(new RuntimeException("mentored error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> lessonController.listLessonByMentoredId(mentoredId));

    assertEquals("mentored error", exception.getMessage());
    verify(lessonUseCase).listLessonByMentoredId(mentoredId);
  }

  @Test
  void shouldRegisterMentoredSuccessfully() {
    when(lessonUseCase.registerMentored("lesson-123")).thenReturn(successResponse);

    ResponseEntity<GenericModelResponse> response = lessonController.registerMentored("lesson-123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(successResponse, response.getBody());
    verify(lessonUseCase).registerMentored("lesson-123");
  }

  @Test
  void shouldPropagateExceptionWhenRegisterMentoredFails() {
    when(lessonUseCase.registerMentored("lesson-123"))
        .thenThrow(new RuntimeException("register error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> lessonController.registerMentored("lesson-123"));

    assertEquals("register error", exception.getMessage());
    verify(lessonUseCase).registerMentored("lesson-123");
  }

  @Test
  void shouldListAllLessonsSuccessfully() {
    LocalDate filterDate = LocalDate.of(2025, 5, 10);
    when(lessonUseCase.listLesson("Java", filterDate, "asc", 0, 10)).thenReturn(pagedResponse);

    ResponseEntity<PagedModelResponse<LessonModelResponse>> response =
        lessonController.listLessons("Java", filterDate, "asc", 0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pagedResponse, response.getBody());
    verify(lessonUseCase).listLesson("Java", filterDate, "asc", 0, 10);
  }

  @Test
  void shouldReturnEmptyPagedResponseWhenNoLessonsFound() {
    PagedModelResponse<LessonModelResponse> emptyPagedResponse = new PagedModelResponse<>();
    emptyPagedResponse.setContent(Collections.emptyList());
    emptyPagedResponse.setSize(10);
    emptyPagedResponse.setTotal(0);
    emptyPagedResponse.setTotalPages(0);
    emptyPagedResponse.setCurrentPage(0);

    LocalDate filterDate = LocalDate.of(2025, 6, 1);
    when(lessonUseCase.listLesson("Kotlin", filterDate, "asc", 0, 10))
        .thenReturn(emptyPagedResponse);

    ResponseEntity<PagedModelResponse<LessonModelResponse>> response =
        lessonController.listLessons("Kotlin", filterDate, "asc", 0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getContent().isEmpty());
    verify(lessonUseCase).listLesson("Kotlin", filterDate, "asc", 0, 10);
  }

  @Test
  void shouldListLessonsWithNullFilters() {
    when(lessonUseCase.listLesson(null, null, "desc", 2, 5)).thenReturn(pagedResponse);

    ResponseEntity<PagedModelResponse<LessonModelResponse>> response =
        lessonController.listLessons(null, null, "desc", 2, 5);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pagedResponse, response.getBody());
    verify(lessonUseCase).listLesson(null, null, "desc", 2, 5);
  }

  @Test
  void shouldPropagateExceptionWhenListingLessonsFails() {
    when(lessonUseCase.listLesson("Java", null, "asc", 0, 10))
        .thenThrow(new RuntimeException("list error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> lessonController.listLessons("Java", null, "asc", 0, 10));

    assertEquals("list error", exception.getMessage());
    verify(lessonUseCase).listLesson("Java", null, "asc", 0, 10);
  }

  @Test
  void shouldValidatePresenceAndGenerateCertificateSuccessfully() {
    when(certificateUseCase.validatePresenceAndGenerateCertificate(lessonUuid, presenceRequest))
        .thenReturn(certificateCreatedResponse);

    ResponseEntity<GenericModelResponse> response =
        lessonController.validatePresence(lessonUuid, presenceRequest);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(certificateCreatedResponse, response.getBody());
    verify(certificateUseCase).validatePresenceAndGenerateCertificate(lessonUuid, presenceRequest);
  }

  @Test
  void shouldPropagateExceptionWhenValidatePresenceFails() {
    when(certificateUseCase.validatePresenceAndGenerateCertificate(lessonUuid, presenceRequest))
        .thenThrow(new RuntimeException("validate error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> lessonController.validatePresence(lessonUuid, presenceRequest));

    assertEquals("validate error", exception.getMessage());
    verify(certificateUseCase).validatePresenceAndGenerateCertificate(lessonUuid, presenceRequest);
  }

  @Test
  void shouldRegenerateCertificateSuccessfully() {
    when(certificateUseCase.regenerateCertificate(lessonUuid)).thenReturn(successResponse);

    ResponseEntity<GenericModelResponse> response =
        lessonController.regenerateCertificate(lessonUuid);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(successResponse, response.getBody());
    verify(certificateUseCase).regenerateCertificate(lessonUuid);
  }

  @Test
  void shouldPropagateExceptionWhenRegenerateCertificateFails() {
    when(certificateUseCase.regenerateCertificate(lessonUuid))
        .thenThrow(new RuntimeException("regenerate error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> lessonController.regenerateCertificate(lessonUuid));

    assertEquals("regenerate error", exception.getMessage());
    verify(certificateUseCase).regenerateCertificate(lessonUuid);
  }

  @Test
  void shouldCountUpcomingLessonsSuccessfully() {
    when(lessonUseCase.countUpcomingAndUnavailabLessons()).thenReturn(countLessonsResponse);

    ResponseEntity<CountLessonsResponse> response = lessonController.countUpcomingLessons();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(countLessonsResponse, response.getBody());
    verify(lessonUseCase).countUpcomingAndUnavailabLessons();
  }

  @Test
  void shouldPropagateExceptionWhenCountUpcomingLessonsFails() {
    when(lessonUseCase.countUpcomingAndUnavailabLessons())
        .thenThrow(new RuntimeException("count error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> lessonController.countUpcomingLessons());

    assertEquals("count error", exception.getMessage());
    verify(lessonUseCase).countUpcomingAndUnavailabLessons();
  }
}
