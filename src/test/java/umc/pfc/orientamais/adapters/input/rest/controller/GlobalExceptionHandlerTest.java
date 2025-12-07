package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private BindingResult bindingResult;

    private FieldError emailFieldError;
    private FieldError passwordFieldError;
    private FieldError duplicateFieldErrorFirst;
    private FieldError duplicateFieldErrorSecond;

    @BeforeEach
    void setUp() {
        emailFieldError = new FieldError("object", "email", "Email inválido");
        passwordFieldError = new FieldError("object", "password", "Senha obrigatória");
        duplicateFieldErrorFirst = new FieldError("object", "email", "Primeira mensagem");
        duplicateFieldErrorSecond = new FieldError("object", "email", "Segunda mensagem");
    }

    @Test
    void shouldHandleValidationExceptionWithMultipleFields() {
        MethodArgumentNotValidException exception = buildValidationException(List.of(emailFieldError, passwordFieldError));

        ResponseEntity<GenericModelResponse> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        GenericModelResponse body = response.getBody();
        assertEquals("VALIDATION_ERROR", body.getCode());
        assertEquals("Erro de validação nos campos", body.getMessage());
        assertNotNull(body.getTimestamp());
        Map<String, String> errors = castToMap(body.getData());
        assertEquals("Email inválido", errors.get("email"));
        assertEquals("Senha obrigatória", errors.get("password"));
        verify(bindingResult).getFieldErrors();
    }

    @Test
    void shouldHandleValidationExceptionWithDuplicateFieldNamesKeepingFirstMessage() {
        MethodArgumentNotValidException exception = buildValidationException(List.of(duplicateFieldErrorFirst, duplicateFieldErrorSecond));

        ResponseEntity<GenericModelResponse> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        Map<String, String> errors = castToMap(body.getData());
        assertEquals(1, errors.size());
        assertEquals("Primeira mensagem", errors.get("email"));
        verify(bindingResult).getFieldErrors();
    }

    @Test
    void shouldHandleValidationExceptionWithEmptyFields() {
        MethodArgumentNotValidException exception = buildValidationException(List.of());

        ResponseEntity<GenericModelResponse> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("VALIDATION_ERROR", body.getCode());
        assertTrue(castToMap(body.getData()).isEmpty());
        verify(bindingResult).getFieldErrors();
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
        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldHandleConstraintViolationWithNullMessage() {
        ConstraintViolationException ex = new ConstraintViolationException(null, Set.of());

        ResponseEntity<GenericModelResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<GenericModelResponse> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INVALID_ARGUMENT", body.getCode());
        assertEquals("Argumento inválido", body.getMessage());
    }

    @Test
    void shouldHandleInvalidOrExpiredTokenException() {
        InvalidOrExpiredTokenException ex = new InvalidOrExpiredTokenException();

        ResponseEntity<GenericModelResponse> response = handler.handleInvalidOrExpiredToken(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INVALID_TOKEN", body.getCode());
        assertEquals("O token fornecido é inválido ou expirou!", body.getMessage());
    }

    @Test
    void shouldHandleBadRequestDomainException() {
        BadRequestException ex = new BadRequestException("Dados inválidos");

        ResponseEntity<GenericModelResponse> response = handler.handleInvalidOrExpiredToken(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("BAD_REQUEST", body.getCode());
        assertEquals("Dados inválidos", body.getMessage());
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
    void shouldHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON inválido");

        ResponseEntity<GenericModelResponse> response = handler.handleBadRequestExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("BAD_REQUEST", body.getCode());
        assertEquals("JSON inválido", body.getMessage());
    }

    @Test
    void shouldHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "Long");

        ResponseEntity<GenericModelResponse> response = handler.handleBadRequestExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("BAD_REQUEST", body.getCode());
        assertTrue(body.getMessage().contains("id"));
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("abc", Integer.class, "age", null, new IllegalArgumentException("Tipo inválido"));

        ResponseEntity<GenericModelResponse> response = handler.handleBadRequestExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("BAD_REQUEST", body.getCode());
        assertTrue(body.getMessage().contains("age"));
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
    void shouldHandleGenericException() {
        Exception ex = new Exception("Erro inesperado no servidor");

        ResponseEntity<GenericModelResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INTERNAL_ERROR", body.getCode());
        assertEquals("Ocorreu um erro inesperado no servidor", body.getMessage());
    }

    @Test
    void shouldContainCurrentTimestampInEveryResponse() {
        Instant before = Instant.now();
        Exception ex = new Exception("Teste de timestamp");

        ResponseEntity<GenericModelResponse> response = handler.handleGenericException(ex);

        GenericModelResponse body = response.getBody();
        assertNotNull(body);
        Instant timestamp = body.getTimestamp();
        assertNotNull(timestamp);
        assertFalse(timestamp.isBefore(before));
        assertFalse(timestamp.isAfter(Instant.now().plusSeconds(1)));
    }

    private MethodArgumentNotValidException buildValidationException(List<FieldError> fieldErrors) {
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        return new MethodArgumentNotValidException(null, bindingResult);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castToMap(Object data) {
        return (Map<String, String>) data;
    }
}
