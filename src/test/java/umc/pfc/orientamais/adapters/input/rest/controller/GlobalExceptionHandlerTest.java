package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "email", "Email inválido"),
                new FieldError("object", "password", "Senha obrigatória")
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("errors"));
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Email inválido", errors.get("email"));
        assertEquals("Senha obrigatória", errors.get("password"));
    }

    @Test
    void handleBusinessExceptions_shouldHandleInvalidOrExpiredTokenException() {
        InvalidOrExpiredTokenException ex = new InvalidOrExpiredTokenException();

        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The provided registration token is invalid or expired.", response.getBody().get("error"));
    }

    @Test
    void handleBusinessExceptions_shouldHandleEmailAlreadyExistsException() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("E-mail já cadastrado");

        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-mail já cadastrado", response.getBody().get("error"));
    }

    @Test
    void handleBusinessExceptions_shouldHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");

        ResponseEntity<Map<String, Object>> response = handler.handleBusinessExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Recurso não encontrado", response.getBody().get("error"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado", response.getBody().get("error"));
    }
}
