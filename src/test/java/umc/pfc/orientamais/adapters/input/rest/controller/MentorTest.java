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
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.port.input.RegisterUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorTest {

    @InjectMocks
    private Mentor mentorController;

    @Mock
    private ValidateEmailUseCase validateEmailUseCase;

    @Mock
    private RegisterUseCase registerUseCase;

    private EmailModelRequest emailRequest;
    private UserRegisterModelRequest registerRequest;

    @BeforeEach
    void setUp() {
        emailRequest = new EmailModelRequest("mentor@email.com");
        registerRequest = new UserRegisterModelRequest(
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
                "token123"
        );
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
                .when(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTOR);

        assertThrows(InvalidOrExpiredTokenException.class, () ->
                mentorController.validateEmail(emailRequest));

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
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);

        assertThrows(EmailAlreadyExistsException.class, () ->
                mentorController.register(registerRequest));

        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);
    }

    @Test
    void shouldThrowWhenRegisterFailsWithInternalError() {
        doThrow(new InternalErrorException("Database error"))
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);

        assertThrows(InternalErrorException.class, () ->
                mentorController.register(registerRequest));

        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTOR);
    }
}
