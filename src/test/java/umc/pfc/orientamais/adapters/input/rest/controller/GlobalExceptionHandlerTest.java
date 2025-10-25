package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleValidationExceptionWithMultipleFields() {
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("object", "email", "Email inválido"),
                new FieldError("object", "password", "Senha obrigatória")
        );

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GenericModelResponse> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        GenericModelResponse body = response.getBody();
        assertEquals("VALIDATION_ERROR", body.getCode());
        assertEquals("Erro de validação nos campos", body.getMessage());
        assertNotNull(body.getTimestamp());

        Map<String, String> errors = (Map<String, String>) body.getData();
        assertEquals("Email inválido", errors.get("email"));
        assertEquals("Senha obrigatória", errors.get("password"));
    }

    @Test
    void shouldHandleValidationExceptionWithEmptyFields() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GenericModelResponse> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("VALIDATION_ERROR", body.getCode());
        assertTrue(((Map<?, ?>) body.getData()).isEmpty());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException("Campo inválido", Set.of());

        ResponseEntity<GenericModelResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("CONSTRAINT_VIOLATION", body.getCode());
        assertEquals("Campo inválido", body.getMessage());
    }

    @Test
    void shouldHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro inesperado");

        ResponseEntity<GenericModelResponse> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("RUNTIME_ERROR", body.getCode());
        assertEquals("Erro inesperado", body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleRuntimeExceptionWithNullMessage() {
        RuntimeException ex = new RuntimeException((String) null);

        ResponseEntity<GenericModelResponse> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("RUNTIME_ERROR", body.getCode());
        assertEquals("Ocorreu um erro inesperado", body.getMessage());
    }

    @Test
    void shouldHandleInvalidOrExpiredTokenException() {
        InvalidOrExpiredTokenException ex = new InvalidOrExpiredTokenException();

        ResponseEntity<GenericModelResponse> response = handler.handleInvalidOrExpiredToken(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INVALID_TOKEN", body.getCode());
        assertEquals("O token fornecido é inválido ou expirou!", body.getMessage());
    }

    @Test
    void shouldHandleEmailAlreadyExistsException() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email já cadastrado");

        ResponseEntity<GenericModelResponse> response = handler.handleEmailAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("EMAIL_EXISTS", body.getCode());
        assertEquals("Email já cadastrado", body.getMessage());
    }

    @Test
    void shouldHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");

        ResponseEntity<GenericModelResponse> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("NOT_FOUND", body.getCode());
        assertEquals("Recurso não encontrado", body.getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Erro inesperado no servidor");

        ResponseEntity<GenericModelResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INTERNAL_ERROR", body.getCode());
        assertEquals("Ocorreu um erro inesperado no servidor", body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldContainCurrentTimestampInEveryResponse() {
        Instant before = Instant.now();
        Exception ex = new Exception("Teste de timestamp");

        ResponseEntity<GenericModelResponse> response = handler.handleGenericException(ex);

        assertNotNull(response.getBody());
        Instant ts = response.getBody().getTimestamp();
        assertNotNull(ts);
        assertFalse(ts.isBefore(before));
        assertFalse(ts.isAfter(Instant.now().plusSeconds(1)));
    }
}
