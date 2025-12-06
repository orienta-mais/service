package umc.pfc.orientamais.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.application.port.input.MentorReviewUseCase;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.port.input.RegisterUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

@ExtendWith(MockitoExtension.class)
class MentorTest {

  @InjectMocks private Mentor mentorController;

  @Mock private ValidateEmailUseCase validateEmailUseCase;

  @Mock private RegisterUseCase registerUseCase;

  @Mock private MentorUseCase mentorUseCase;

  @Mock private MentorReviewUseCase mentorReviewUseCase;

  private EmailModelRequest emailRequest;
  private UserRegisterModelRequest registerRequest;
  private MentorUpdateModelRequest updateRequest;
  private MentorReviewRequest mentorReviewRequest;
  private GenericModelResponse successResponse;
  private MentorModelResponse mentorModelResponse;
  private MentorReviewResponse mentorReviewResponse;
  private UUID mentorId;

  @BeforeEach
  void setUp() {
    mentorId = UUID.randomUUID();
    emailRequest = new EmailModelRequest("mentor@email.com");
    registerRequest =
        new UserRegisterModelRequest(
            "mentor@email.com",
            "Name",
            "Last Name",
            "12345678",
            LocalDate.of(2002, 6, 21),
            null,
            null,
            "SP",
            "BR",
            AuthUserRole.MENTOR,
            "token123");
    updateRequest =
        new MentorUpdateModelRequest(
            "Updated", "Mentor", LocalDate.of(1990, 1, 1), "twitter", "Description", "SP", "BR");

    mentorReviewRequest = new MentorReviewRequest(5, 4, 5, 4, 5, "Ótimo mentor");
    successResponse = new GenericModelResponse("SUCCESS", "All good");

    mentorModelResponse = new MentorModelResponse();
    mentorModelResponse.setId(UUID.randomUUID());
    mentorModelResponse.setName("Mentor");
    mentorModelResponse.setLastName("One");

    mentorReviewResponse =
        new MentorReviewResponse(
            UUID.randomUUID(), mentorId, UUID.randomUUID(), "Mentored", 5, 4, 5, 4, 5, "Great");
  }

  @Test
  void shouldValidateEmailSuccessfully() {
    doNothing().when(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTOR);

    ResponseEntity<GenericModelResponse> response = mentorController.validateEmail(emailRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("EMAIL_VALIDATED", response.getBody().getCode());
    assertEquals("Validation link sent to email", response.getBody().getMessage());
    verify(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTOR);
  }

  @Test
  void shouldThrowWhenValidateEmailFails() {
    doThrow(new InvalidOrExpiredTokenException())
        .when(validateEmailUseCase)
        .validateAndSendLink(emailRequest, AuthUserRole.MENTOR);

    assertThrows(
        InvalidOrExpiredTokenException.class, () -> mentorController.validateEmail(emailRequest));

    verify(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTOR);
  }

  @Test
  void shouldRegisterUserSuccessfully() {
    doNothing().when(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);

    ResponseEntity<GenericModelResponse> response = mentorController.register(registerRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("USER_CREATED", response.getBody().getCode());
    assertEquals("User registered successfully", response.getBody().getMessage());
    verify(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);
  }

  @Test
  void shouldThrowWhenRegisterFailsDueToExistingEmail() {
    doThrow(new EmailAlreadyExistsException("Email already exists"))
        .when(registerUseCase)
        .register(registerRequest, AuthUserRole.MENTOR);

    assertThrows(
        EmailAlreadyExistsException.class, () -> mentorController.register(registerRequest));

    verify(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);
  }

  @Test
  void shouldThrowWhenRegisterFailsWithInternalError() {
    doThrow(new InternalErrorException("Database error"))
        .when(registerUseCase)
        .register(registerRequest, AuthUserRole.MENTOR);

    assertThrows(InternalErrorException.class, () -> mentorController.register(registerRequest));

    verify(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);
  }

  @Test
  void shouldGetAllMentorsSuccessfully() {
    when(mentorUseCase.getAllMentors()).thenReturn(List.of(mentorModelResponse));

    ResponseEntity<List<MentorModelResponse>> response = mentorController.getAllMentors();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    verify(mentorUseCase).getAllMentors();
  }

  @Test
  void shouldReturnEmptyListWhenNoMentorsAreRegistered() {
    when(mentorUseCase.getAllMentors()).thenReturn(Collections.emptyList());

    ResponseEntity<List<MentorModelResponse>> response = mentorController.getAllMentors();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
    verify(mentorUseCase).getAllMentors();
  }

  @Test
  void shouldPropagateExceptionWhenGetAllMentorsFails() {
    when(mentorUseCase.getAllMentors()).thenThrow(new RuntimeException("error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> mentorController.getAllMentors());

    assertEquals("error", exception.getMessage());
    verify(mentorUseCase).getAllMentors();
  }

  @Test
  void shouldGetMentorByIdSuccessfully() {
    when(mentorUseCase.getMentorById(mentorId)).thenReturn(mentorModelResponse);

    ResponseEntity<MentorModelResponse> response = mentorController.getMentorById(mentorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mentorModelResponse, response.getBody());
    verify(mentorUseCase).getMentorById(mentorId);
  }

  @Test
  void shouldPropagateExceptionWhenGetMentorByIdFails() {
    when(mentorUseCase.getMentorById(mentorId)).thenThrow(new RuntimeException("not found"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> mentorController.getMentorById(mentorId));

    assertEquals("not found", exception.getMessage());
    verify(mentorUseCase).getMentorById(mentorId);
  }

  @Test
  void shouldUpdateMentorSuccessfully() {
    when(mentorUseCase.updateMentor(mentorId, updateRequest)).thenReturn(mentorModelResponse);

    ResponseEntity<MentorModelResponse> response =
        mentorController.updateMentor(mentorId, updateRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mentorModelResponse, response.getBody());
    verify(mentorUseCase).updateMentor(mentorId, updateRequest);
  }

  @Test
  void shouldPropagateExceptionWhenUpdateMentorFails() {
    when(mentorUseCase.updateMentor(mentorId, updateRequest))
        .thenThrow(new IllegalArgumentException("invalid"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> mentorController.updateMentor(mentorId, updateRequest));

    assertEquals("invalid", exception.getMessage());
    verify(mentorUseCase).updateMentor(mentorId, updateRequest);
  }

  @Test
  void shouldDeleteMentorSuccessfully() {
    ResponseEntity<Void> response = mentorController.deleteMentor(mentorId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(mentorUseCase).deleteMentor(mentorId);
  }

  @Test
  void shouldPropagateExceptionWhenDeleteMentorFails() {
    doThrow(new RuntimeException("delete error")).when(mentorUseCase).deleteMentor(mentorId);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> mentorController.deleteMentor(mentorId));

    assertEquals("delete error", exception.getMessage());
    verify(mentorUseCase).deleteMentor(mentorId);
  }

  @Test
  void shouldAddMentorReviewSuccessfully() {
    when(mentorReviewUseCase.addMentorReview(mentorId, mentorReviewRequest))
        .thenReturn(successResponse);

    ResponseEntity<GenericModelResponse> response =
        mentorController.addMentorReview(mentorId, mentorReviewRequest);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(successResponse, response.getBody());
    verify(mentorReviewUseCase).addMentorReview(mentorId, mentorReviewRequest);
  }

  @Test
  void shouldPropagateExceptionWhenAddMentorReviewFails() {
    when(mentorReviewUseCase.addMentorReview(mentorId, mentorReviewRequest))
        .thenThrow(new RuntimeException("review error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> mentorController.addMentorReview(mentorId, mentorReviewRequest));

    assertEquals("review error", exception.getMessage());
    verify(mentorReviewUseCase).addMentorReview(mentorId, mentorReviewRequest);
  }

  @Test
  void shouldListReviewsSuccessfully() {
    when(mentorReviewUseCase.listMentorReviews(mentorId)).thenReturn(List.of(mentorReviewResponse));

    ResponseEntity<List<MentorReviewResponse>> response = mentorController.listReviews(mentorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    verify(mentorReviewUseCase).listMentorReviews(mentorId);
  }

  @Test
  void shouldReturnEmptyReviewListWhenNoneFound() {
    when(mentorReviewUseCase.listMentorReviews(mentorId)).thenReturn(Collections.emptyList());

    ResponseEntity<List<MentorReviewResponse>> response = mentorController.listReviews(mentorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
    verify(mentorReviewUseCase).listMentorReviews(mentorId);
  }

  @Test
  void shouldPropagateExceptionWhenListReviewsFails() {
    when(mentorReviewUseCase.listMentorReviews(mentorId))
        .thenThrow(new RuntimeException("list error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> mentorController.listReviews(mentorId));

    assertEquals("list error", exception.getMessage());
    verify(mentorReviewUseCase).listMentorReviews(mentorId);
  }
}
