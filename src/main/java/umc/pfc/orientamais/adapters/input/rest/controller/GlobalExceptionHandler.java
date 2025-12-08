package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.application.service.utils.TimeUtils;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.EmailAlreadyExistsException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GenericModelResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (msg1, msg2) -> msg1));

    log.warn("Erro de validação: {}", fieldErrors);
    return ResponseEntity.badRequest()
        .body(
            GenericModelResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Erro de validação nos campos")
                .data(fieldErrors)
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<GenericModelResponse> handleConstraintViolation(
      ConstraintViolationException ex) {
    log.warn("Violação de restrição: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(
            GenericModelResponse.builder()
                .code("CONSTRAINT_VIOLATION")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GenericModelResponse> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    log.warn("Argumento inválido: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(
            GenericModelResponse.builder()
                .code("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(InvalidOrExpiredTokenException.class)
  public ResponseEntity<GenericModelResponse> handleInvalidOrExpiredToken(
      InvalidOrExpiredTokenException ex) {
    log.warn("Token inválido ou expirado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            GenericModelResponse.builder()
                .code("INVALID_TOKEN")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<GenericModelResponse> handleInvalidOrExpiredToken(BadRequestException ex) {
    log.warn("Bad request: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            GenericModelResponse.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<GenericModelResponse> handleEmailAlreadyExists(
      EmailAlreadyExistsException ex) {
    log.warn("Email duplicado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            GenericModelResponse.builder()
                .code("EMAIL_EXISTS")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<GenericModelResponse> handleNotFound(NotFoundException ex) {
    log.warn("Recurso não encontrado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            GenericModelResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<GenericModelResponse> handleBadRequestExceptions(Exception ex) {
    log.warn("Requisição inválida: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(
            GenericModelResponse.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<GenericModelResponse> handleRuntimeException(RuntimeException ex) {
    log.error("Erro em tempo de execução: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            GenericModelResponse.builder()
                .code("RUNTIME_ERROR")
                .message(ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado")
                .timestamp(TimeUtils.nowUtc())
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericModelResponse> handleGenericException(Exception ex) {
    log.error("Erro inesperado: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            GenericModelResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Ocorreu um erro inesperado no servidor")
                .timestamp(TimeUtils.nowUtc())
                .build());
  }
}
