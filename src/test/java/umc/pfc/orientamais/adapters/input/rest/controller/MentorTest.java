package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.port.input.RegisterMentorUseCase;
import umc.pfc.orientamais.application.port.input.ValidateEmailUseCase;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MentorTest {

    @InjectMocks
    private Mentor mentorController;

    @Mock
    private ValidateEmailUseCase validateEmailUseCase;

    @Mock
    private RegisterMentorUseCase registerMentorUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateEmail_Success() {
        EmailModelRequest request = new EmailModelRequest("test@example.com");

        ResponseEntity<GenericModelResponse> response = mentorController.validateEmail(request);

        verify(validateEmailUseCase, times(1)).validateAndSendLink(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("EMAIL_VALIDATED", response.getBody().getCode());
        assertEquals("Validation link sent to email", response.getBody().getMessage());
    }

//    @Test
//    void testValidateEmail_Exception() {
//        EmailModelRequest request = new EmailModelRequest("test@example.com");
//        doThrow(new RuntimeException("Invalid email")).when(validateEmailUseCase).validateAndSendLink(request);
//
//        ResponseEntity<GenericModelResponse> response = mentorController.validateEmail(request);
//
//        verify(validateEmailUseCase, times(1)).validateAndSendLink(request);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        Assertions.assertNotNull(response.getBody());
//        assertEquals("ERROR", response.getBody().getCode());
//        assertEquals("Error creating leason: Invalid email", response.getBody().getMessage());
//    }

    @Test
    void testRegister_Success() {
        UserRegisterModelRequest request = new UserRegisterModelRequest(
                "test@example.com",
                "John",
                "Doe",
                "password123",
                LocalDate.of(1990, 1, 1),
                "instagram.com/johndoe",
                "Experienced mentor",
                "SP",
                "Brazil",
                "MENTOR",
                "sample-token-123"
        );

        ResponseEntity<GenericModelResponse> response = mentorController.register(request);

        verify(registerMentorUseCase, times(1)).register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("USER_CREATED", response.getBody().getCode());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }

//    @Test
//    void testRegister_Exception() {
//        UserRegisterModelRequest request = new UserRegisterModelRequest(
//                "test@example.com",
//                "John",
//                "Doe",
//                "password123",
//                LocalDate.of(1990, 1, 1),
//                "instagram.com/johndoe",
//                "Experienced mentor",
//                "SP",
//                "Brazil",
//                "MENTOR",
//                "sample-token-123"
//        );
//
//        doThrow(new RuntimeException("Email already exists")).when(registerMentorUseCase).register(request);
//
//        ResponseEntity<GenericModelResponse> response = mentorController.register(request);
//
//        verify(registerMentorUseCase, times(1)).register(request);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        Assertions.assertNotNull(response.getBody());
//        assertEquals("ERROR", response.getBody().getCode());
//        assertEquals("Error creating leason: Email already exists", response.getBody().getMessage());
//    }
}
