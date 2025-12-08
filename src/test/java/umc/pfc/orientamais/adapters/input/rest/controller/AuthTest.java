package umc.pfc.orientamais.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ChangePasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.LoginRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ResetPasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.application.port.input.LoginUseCase;
import umc.pfc.orientamais.application.port.input.PasswordResetUseCase;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthTest {

  @InjectMocks private Auth auth;

  @Mock private LoginUseCase loginUseCase;

  @Mock private PasswordResetUseCase passwordResetUseCase;

  @Mock private HttpServletRequest httpServletRequest;

  private LoginRequest loginRequest;
  private LoginResponse loginResponse;
  private EmailModelRequest emailModelRequest;
  private ResetPasswordModelRequest resetPasswordModelRequest;
  private ChangePasswordModelRequest changePasswordModelRequest;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest("user@email.com", "123456");
    loginResponse =
        new LoginResponse(
            "SUCCESS", "Login realizado com sucesso", "accessToken", "refreshToken", true);
    emailModelRequest = new EmailModelRequest("user@email.com");
    resetPasswordModelRequest =
        new ResetPasswordModelRequest("token", "user@email.com", "newPassword123", "newPassword123");
    changePasswordModelRequest =
        new ChangePasswordModelRequest("user@email.com", "currentPassword123", "newPassword456");
  }

  @Test
  void shouldLoginSuccessfully() {
    when(loginUseCase.login("user@email.com", "123456")).thenReturn(loginResponse);

    ResponseEntity<LoginResponse> response = auth.login(loginRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("SUCCESS", response.getBody().status());
    assertEquals("Login realizado com sucesso", response.getBody().message());
    assertEquals("accessToken", response.getBody().accessToken());
    assertEquals("refreshToken", response.getBody().refreshToken());
    verify(loginUseCase).login("user@email.com", "123456");
  }

  @Test
  void shouldThrowWhenLoginFails() {
    when(loginUseCase.login("user@email.com", "123456"))
        .thenThrow(new NotFoundException("Invalid credentials"));

    assertThrows(NotFoundException.class, () -> auth.login(loginRequest));
    verify(loginUseCase).login("user@email.com", "123456");
  }

  @Test
  void shouldThrowNullPointerWhenRequestIsNull() {
    assertThrows(NullPointerException.class, () -> auth.login(null));
  }

  @Test
  void shouldRefreshTokenSuccessfully() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
    when(loginUseCase.refreshToken("validRefreshToken")).thenReturn(loginResponse);

    ResponseEntity<LoginResponse> response = auth.refreshToken(httpServletRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("SUCCESS", response.getBody().status());
    assertEquals("Login realizado com sucesso", response.getBody().message());
    assertEquals("accessToken", response.getBody().accessToken());
    assertEquals("refreshToken", response.getBody().refreshToken());
    verify(loginUseCase).refreshToken("validRefreshToken");
  }

  @Test
  void shouldThrowWhenAuthorizationHeaderIsMissing() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

    assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
    verify(loginUseCase, never()).refreshToken(anyString());
  }

  @Test
  void shouldThrowWhenAuthorizationHeaderHasInvalidPrefix() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Token abc123");

    assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
    verify(loginUseCase, never()).refreshToken(anyString());
  }

  @Test
  void shouldThrowWhenRefreshTokenIsExpired() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer expiredToken");
    when(loginUseCase.refreshToken("expiredToken")).thenThrow(new InvalidOrExpiredTokenException());

    assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
    verify(loginUseCase).refreshToken("expiredToken");
  }

  @Test
  void shouldThrowWhenBearerHasNoToken() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");

    assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
    verify(loginUseCase, never()).refreshToken(anyString());
  }

  @Test
  void shouldHandleBearerWithExtraSpaces() {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("  Bearer   token123  ");

    assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
    verify(loginUseCase, never()).refreshToken(anyString());
  }

  @Test
  void shouldRequestPasswordResetSuccessfully() {
    ResponseEntity<GenericModelResponse> response = auth.requestPasswordReset(emailModelRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("PASSWORD_RESET_SUCCESS", response.getBody().getCode());
    assertEquals("Password reset link sent to email", response.getBody().getMessage());
    verify(passwordResetUseCase).requestPasswordReset(emailModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenRequestPasswordResetFails() {
    doThrow(new NotFoundException("Email not found"))
        .when(passwordResetUseCase)
        .requestPasswordReset(emailModelRequest);

    assertThrows(NotFoundException.class, () -> auth.requestPasswordReset(emailModelRequest));
    verify(passwordResetUseCase).requestPasswordReset(emailModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenPasswordResetRequestIsNull() {
    doThrow(new NullPointerException("request"))
        .when(passwordResetUseCase)
        .requestPasswordReset(null);

    assertThrows(NullPointerException.class, () -> auth.requestPasswordReset(null));
    verify(passwordResetUseCase).requestPasswordReset(null);
  }

  @Test
  void shouldResetPasswordSuccessfully() {
    ResponseEntity<GenericModelResponse> response = auth.resetPassword(resetPasswordModelRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("PASSWORD_RESET_SUCCESS", response.getBody().getCode());
    assertEquals("Password has been reset successfully", response.getBody().getMessage());
    verify(passwordResetUseCase).resetPassword(resetPasswordModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenResetPasswordFails() {
    doThrow(new InvalidOrExpiredTokenException())
        .when(passwordResetUseCase)
        .resetPassword(resetPasswordModelRequest);

    assertThrows(
        InvalidOrExpiredTokenException.class, () -> auth.resetPassword(resetPasswordModelRequest));
    verify(passwordResetUseCase).resetPassword(resetPasswordModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenResetPasswordRequestIsNull() {
    doThrow(new NullPointerException("request")).when(passwordResetUseCase).resetPassword(null);

    assertThrows(NullPointerException.class, () -> auth.resetPassword(null));
    verify(passwordResetUseCase).resetPassword(null);
  }

  @Test
  void shouldChangePasswordSuccessfully() {
    ResponseEntity<GenericModelResponse> response = auth.changePassword(changePasswordModelRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    assertEquals("PASSWORD_CHANGE_SUCCESS", response.getBody().getCode());
    assertEquals("Password changed successfully", response.getBody().getMessage());
    verify(passwordResetUseCase).changePassword(changePasswordModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenChangePasswordFails() {
    doThrow(new NotFoundException("User not found"))
        .when(passwordResetUseCase)
        .changePassword(changePasswordModelRequest);

    assertThrows(NotFoundException.class, () -> auth.changePassword(changePasswordModelRequest));
    verify(passwordResetUseCase).changePassword(changePasswordModelRequest);
  }

  @Test
  void shouldPropagateExceptionWhenChangePasswordRequestIsNull() {
    doThrow(new NullPointerException("request")).when(passwordResetUseCase).changePassword(null);

    assertThrows(NullPointerException.class, () -> auth.changePassword(null));
    verify(passwordResetUseCase).changePassword(null);
  }
}
