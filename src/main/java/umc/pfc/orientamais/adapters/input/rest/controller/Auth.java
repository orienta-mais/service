package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ChangePasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.LoginRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ResetPasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.application.port.input.LoginUseCase;
import umc.pfc.orientamais.application.port.input.PasswordResetUseCase;
import umc.pfc.orientamais.application.port.input.TermsAndPrivacyUseCase;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Auth {

  private final LoginUseCase loginUseCase;
  private final PasswordResetUseCase passwordResetUseCase;
  private final TermsAndPrivacyUseCase termsAndPrivacyUseCase;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(loginUseCase.login(request.email(), request.password()));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new InvalidOrExpiredTokenException();
    }

    String refreshToken = authHeader.substring(7).trim();
    if (refreshToken.isEmpty()) {
      throw new InvalidOrExpiredTokenException();
    }

    LoginResponse response = loginUseCase.refreshToken(refreshToken);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/forget-password")
  public ResponseEntity<GenericModelResponse> requestPasswordReset(
      @RequestBody @Valid EmailModelRequest request) {
    passwordResetUseCase.requestPasswordReset(request);
    return ResponseEntity.ok(
        new GenericModelResponse("PASSWORD_RESET_SUCCESS", "Password reset link sent to email"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<GenericModelResponse> resetPassword(
      @RequestBody @Valid ResetPasswordModelRequest request) {
    passwordResetUseCase.resetPassword(request);
    return ResponseEntity.ok(
        new GenericModelResponse("PASSWORD_RESET_SUCCESS", "Password has been reset successfully"));
  }

  @PostMapping("/change-password")
  public ResponseEntity<GenericModelResponse> changePassword(
      @RequestBody @Valid ChangePasswordModelRequest request) {
    passwordResetUseCase.changePassword(request);
    return ResponseEntity.ok(
        new GenericModelResponse("PASSWORD_CHANGE_SUCCESS", "Password changed successfully"));
  }

  @PostMapping("/terms/policy/confirm")
  public ResponseEntity<GenericModelResponse> confirmTermsPolicy() {
    termsAndPrivacyUseCase.confirmTermsPolicy();
    return ResponseEntity.ok(
        new GenericModelResponse(
            "TERMS_POLICY_CONFIRMED", "Terms and Policy confirmed successfully"));
  }
}
