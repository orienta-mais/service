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
class MentoredTest {

    @InjectMocks
    private Mentored mentoredController;

    @Mock
    private ValidateEmailUseCase validateEmailUseCase;

    @Mock
    private RegisterUseCase registerUseCase;

    private EmailModelRequest emailRequest;
    private UserRegisterModelRequest registerRequest;

    @BeforeEach
    void setUp() {
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
    void shouldThrowWhenValidateEmailFails() {
        doThrow(new InvalidOrExpiredTokenException())
                .when(validateEmailUseCase).validateAndSendLink(emailRequest, AuthUserRole.MENTORED);

        assertThrows(InvalidOrExpiredTokenException.class, () ->
                mentoredController.validateEmail(emailRequest));

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
    void shouldThrowWhenRegisterFailsDueToExistingEmail() {
        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);

        assertThrows(EmailAlreadyExistsException.class, () ->
                mentoredController.register(registerRequest));

        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);
    }

    @Test
    void shouldThrowWhenRegisterFailsWithInternalError() {
        doThrow(new InternalErrorException("Database error"))
                .when(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);

        assertThrows(InternalErrorException.class, () ->
                mentoredController.register(registerRequest));

        verify(registerUseCase).register(registerRequest, AuthUserRole.MENTORED);
    }
}
