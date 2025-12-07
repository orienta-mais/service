package umc.pfc.orientamais.adapters.input.rest.controller;

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
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorInfoModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;
import umc.pfc.orientamais.application.port.input.*;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentoredTest {

    @InjectMocks
    private Mentored mentoredController;

    @Mock
    private ValidateEmailUseCase validateEmailUseCase;

    @Mock
    private RegisterUseCase registerUseCase;

    @Mock
    private MentoredUseCase mentoredUseCase;

    @Mock
    private MentorUseCase mentorUseCase;

    @Mock
    private MentorReviewUseCase mentorReviewUseCase;

    private EmailModelRequest emailRequest;
    private UserRegisterModelRequest registerRequest;
    private MentoredUpdateModelRequest updateRequest;
    private MentorReviewRequest mentorReviewRequest;
    private GenericModelResponse successResponse;
    private MentoredModelResponse mentoredModelResponse;
    private MentorInfoModelResponse mentorInfoModelResponse;
    private UUID mentoredId;
    private UUID mentorId;

    @BeforeEach
    void setUp() {
        mentoredId = UUID.randomUUID();
        mentorId = UUID.randomUUID();

        emailRequest = new EmailModelRequest("mentored@email.com");
        registerRequest = new UserRegisterModelRequest(
                "mentored@email.com",
                "Name",
                "Last Name",
                "12345678",
                LocalDate.of(2002, 6, 21),
                null,
                null,
                "SP",
                "BR",
                AuthUserRole.MENTORED,
                "token123"
        );
        updateRequest = new MentoredUpdateModelRequest(
                "Updated",
                "Mentored",
                LocalDate.of(1998, 3, 15),
                "social",
                "description",
                "SP",
                "BR"
        );
        mentorReviewRequest = new MentorReviewRequest(5, 4, 5, 4, 5, "Excelente mentor");
        successResponse = new GenericModelResponse("SUCCESS", "Done");

        mentoredModelResponse = new MentoredModelResponse();
        mentoredModelResponse.setId(mentoredId);
        mentoredModelResponse.setName("Mentored");
        mentoredModelResponse.setState("SP");

        mentorInfoModelResponse = new MentorInfoModelResponse();
        mentorInfoModelResponse.setId(mentorId);
        mentorInfoModelResponse.setName("Mentor");
        mentorInfoModelResponse.setCanAddReview(true);
    }

    @Test
    void shouldValidateEmailSuccessfully() {
        doNothing().when(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTORED);

        ResponseEntity<GenericModelResponse> response = mentoredController.validateEmail(emailRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("EMAIL_VALIDATED", response.getBody().getCode());
        assertEquals("Validation link sent to email", response.getBody().getMessage());
        verify(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldPropagateExceptionWhenValidateEmailRequestIsNull() {
        doThrow(new NullPointerException("request"))
                .when(validateEmailUseCase).validateAndSendLink(null, AuthUserRole.MENTORED);

        assertThrows(NullPointerException.class, () -> mentoredController.validateEmail(null));
        verify(validateEmailUseCase).validateAndSendLink(null, AuthUserRole.MENTORED);
    }

    @Test
    void shouldThrowWhenValidateEmailFails() {
        doThrow(new InvalidOrExpiredTokenException())
                .when(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTORED);

        assertThrows(InvalidOrExpiredTokenException.class, () -> mentoredController.validateEmail(emailRequest));
        verify(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        doNothing().when(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);

        ResponseEntity<GenericModelResponse> response = mentoredController.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("USER_CREATED", response.getBody().getCode());
        assertEquals("User registered successfully", response.getBody().getMessage());
        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldPropagateExceptionWhenRegisterRequestIsNull() {
        doThrow(new NullPointerException("request"))
                .when(registerUseCase).register(null, AuthUserRole.MENTORED);

        assertThrows(NullPointerException.class, () -> mentoredController.register(null));
        verify(registerUseCase).register(null, AuthUserRole.MENTORED);
    }

    @Test
    void shouldThrowWhenRegisterFailsDueToExistingEmail() {
        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);

        assertThrows(EmailAlreadyExistsException.class, () -> mentoredController.register(registerRequest));
        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldThrowWhenRegisterFailsWithInternalError() {
        doThrow(new InternalErrorException("Database error"))
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);

        assertThrows(InternalErrorException.class, () -> mentoredController.register(registerRequest));
        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldGetAllMentoredsSuccessfully() {
        when(mentoredUseCase.getAllMentoreds()).thenReturn(List.of(mentoredModelResponse));

        ResponseEntity<List<MentoredModelResponse>> response = mentoredController.getAllMentoreds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(mentoredUseCase).getAllMentoreds();
    }

    @Test
    void shouldReturnEmptyListWhenNoMentoredsFound() {
        when(mentoredUseCase.getAllMentoreds()).thenReturn(Collections.emptyList());

        ResponseEntity<List<MentoredModelResponse>> response = mentoredController.getAllMentoreds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(mentoredUseCase).getAllMentoreds();
    }

    @Test
    void shouldHandleNullResponseWhenListingMentoreds() {
        when(mentoredUseCase.getAllMentoreds()).thenReturn(null);

        ResponseEntity<List<MentoredModelResponse>> response = mentoredController.getAllMentoreds();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(mentoredUseCase).getAllMentoreds();
    }

    @Test
    void shouldPropagateExceptionWhenGettingAllMentoredsFails() {
        when(mentoredUseCase.getAllMentoreds()).thenThrow(new RuntimeException("error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentoredController.getAllMentoreds());
        assertEquals("error", exception.getMessage());
        verify(mentoredUseCase).getAllMentoreds();
    }

    @Test
    void shouldGetMentoredByIdSuccessfully() {
        when(mentoredUseCase.getMentoredById(mentoredId)).thenReturn(mentoredModelResponse);

        ResponseEntity<MentoredModelResponse> response = mentoredController.getMentoredById(mentoredId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mentoredModelResponse, response.getBody());
        verify(mentoredUseCase).getMentoredById(mentoredId);
    }

    @Test
    void shouldPropagateExceptionWhenGetMentoredByIdFails() {
        when(mentoredUseCase.getMentoredById(mentoredId)).thenThrow(new RuntimeException("not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentoredController.getMentoredById(mentoredId));
        assertEquals("not found", exception.getMessage());
        verify(mentoredUseCase).getMentoredById(mentoredId);
    }

    @Test
    void shouldUpdateMentoredSuccessfully() {
        when(mentoredUseCase.updateMentored(mentoredId, updateRequest)).thenReturn(mentoredModelResponse);

        ResponseEntity<MentoredModelResponse> response = mentoredController.updateMentored(mentoredId, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mentoredModelResponse, response.getBody());
        verify(mentoredUseCase).updateMentored(mentoredId, updateRequest);
    }

    @Test
    void shouldPropagateExceptionWhenUpdateMentoredFails() {
        when(mentoredUseCase.updateMentored(mentoredId, updateRequest)).thenThrow(new IllegalArgumentException("invalid"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentoredController.updateMentored(mentoredId, updateRequest));
        assertEquals("invalid", exception.getMessage());
        verify(mentoredUseCase).updateMentored(mentoredId, updateRequest);
    }

    @Test
    void shouldDeleteMentoredSuccessfully() {
        ResponseEntity<Void> response = mentoredController.deleteMentored(mentoredId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(mentoredUseCase).deleteMentored(mentoredId);
    }

    @Test
    void shouldPropagateExceptionWhenDeleteMentoredFails() {
        doThrow(new RuntimeException("delete error")).when(mentoredUseCase).deleteMentored(mentoredId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentoredController.deleteMentored(mentoredId));
        assertEquals("delete error", exception.getMessage());
        verify(mentoredUseCase).deleteMentored(mentoredId);
    }

    @Test
    void shouldGetMentorInfosSuccessfully() {
        when(mentorUseCase.getMentorInfosById(mentorId)).thenReturn(mentorInfoModelResponse);

        ResponseEntity<MentorInfoModelResponse> response = mentoredController.getMentorInfosById(mentorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mentorInfoModelResponse, response.getBody());
        verify(mentorUseCase).getMentorInfosById(mentorId);
    }

    @Test
    void shouldPropagateExceptionWhenGetMentorInfosFails() {
        when(mentorUseCase.getMentorInfosById(mentorId)).thenThrow(new RuntimeException("info error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentoredController.getMentorInfosById(mentorId));
        assertEquals("info error", exception.getMessage());
        verify(mentorUseCase).getMentorInfosById(mentorId);
    }

    @Test
    void shouldAddMentorReviewSuccessfully() {
        when(mentorReviewUseCase.addMentorReview(mentorId, mentorReviewRequest)).thenReturn(successResponse);

        ResponseEntity<GenericModelResponse> response = mentoredController.addMentorReview(mentorId, mentorReviewRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(successResponse, response.getBody());
        verify(mentorReviewUseCase).addMentorReview(mentorId, mentorReviewRequest);
    }

    @Test
    void shouldPropagateExceptionWhenAddMentorReviewFails() {
        when(mentorReviewUseCase.addMentorReview(mentorId, mentorReviewRequest)).thenThrow(new RuntimeException("review error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentoredController.addMentorReview(mentorId, mentorReviewRequest));
        assertEquals("review error", exception.getMessage());
        verify(mentorReviewUseCase).addMentorReview(mentorId, mentorReviewRequest);
    }

    @Test
    void shouldPropagateExceptionWhenMentorReviewRequestIsNull() {
        when(mentorReviewUseCase.addMentorReview(mentorId, null)).thenThrow(new NullPointerException("request"));

        assertThrows(NullPointerException.class, () -> mentoredController.addMentorReview(mentorId, null));
        verify(mentorReviewUseCase).addMentorReview(mentorId, null);
    }
}
