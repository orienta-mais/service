package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("timestamp"));
        assertEquals(400, response.getBody().get("status"));
        assertInstanceOf(Map.class, response.getBody().get("errors"));

        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Email inválido", errors.get("email"));
        assertEquals("Senha obrigatória", errors.get("password"));
    }

    @Test
    void shouldHandleValidationExceptionWithEmptyFields() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertTrue(((Map<?, ?>) body.get("errors")).isEmpty());
    }

    @Test
    void shouldHandleInvalidOrExpiredTokenException() {
        InvalidOrExpiredTokenException ex = new InvalidOrExpiredTokenException();
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("timestamp"));
        assertEquals(400, response.getBody().get("status"));
        assertTrue(response.getBody().get("error").toString().contains("token"));
    }

    @Test
    void shouldHandleEmailAlreadyExistsException() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email já cadastrado");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Email já cadastrado", response.getBody().get("error"));
    }

    @Test
    void shouldHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recurso não encontrado", response.getBody().get("error"));
    }

    @Test
    void shouldHandleBusinessExceptionWithNullMessage() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException(null);
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("error"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Erro inesperado no servidor");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Ocorreu um erro inesperado", response.getBody().get("error"));
    }

    @Test
    void shouldHandleGenericExceptionWithNullMessage() {
        Exception ex = new Exception((String) null);

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocorreu um erro inesperado", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    void shouldContainCurrentTimestampInEveryResponse() {
        LocalDateTime before = LocalDateTime.now();
        Exception ex = new Exception("Test");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response.getBody());
        Object timestamp = response.getBody().get("timestamp");
        assertNotNull(timestamp);
        assertInstanceOf(LocalDateTime.class, timestamp);
        LocalDateTime ts = (LocalDateTime) timestamp;
        assertFalse(ts.isBefore(before.minusSeconds(1)));
        assertFalse(ts.isAfter(after.plusSeconds(1)));
    }
}
