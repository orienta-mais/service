package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ChangePasswordModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.EmailModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.ResetPasswordModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.PasswordResetTokenRepository;
import umc.pfc.orientamais.application.port.input.PasswordResetUseCase;
import umc.pfc.orientamais.application.service.email.EmailSenderService;
import umc.pfc.orientamais.application.service.utils.EmailTemplateBuilder;
import umc.pfc.orientamais.application.service.utils.PasswordResetTokenFactory;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements PasswordResetUseCase {

  private final AuthUserRepository authUserRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordResetTokenFactory tokenFactory;
  private final EmailSenderService emailSender;
  private final EmailTemplateBuilder templateBuilder;
  private final BCryptPasswordEncoder passwordEncoder;

  @Value("${app.reset-password.url}")
  private String resetUrl;

  @Value("${app.reset-password.ttl-seconds:86400}")
  private long tokenTtlSeconds;

  @Override
  public void requestPasswordReset(EmailModelRequest request) {
    boolean exists = authUserRepository.existsByEmail(request.email());
    if (!exists) {
      throw new BadRequestException("Email ou senha inválidos!");
    }

    PasswordResetToken token = tokenFactory.create(request.email(), tokenTtlSeconds);

    try {
      tokenRepository.deleteByEmail(request.email());
      tokenRepository.save(token);
    } catch (Exception e) {
      throw new InternalErrorException("Erro ao gerar token de recuperação");
    }

    String link =
        UriComponentsBuilder.fromUriString(resetUrl)
            .queryParam("token", token.token())
            .queryParam("email", token.email())
            .toUriString();

    String html = templateBuilder.buildPasswordResetEmail(link);

    try {
      emailSender.sendEmail(request.email(), "Recuperação de senha - Orienta+", html);
    } catch (Exception e) {
      tokenRepository.delete(token);
      throw new InternalErrorException("Erro ao enviar email de recuperação");
    }
  }

  @Override
  public void resetPassword(ResetPasswordModelRequest request) {
    PasswordResetToken token =
        tokenRepository
            .findByToken(request.token())
            .orElseThrow(InvalidOrExpiredTokenException::new);

    if (token.isExpired()) {
      tokenRepository.delete(token);
      throw new InvalidOrExpiredTokenException();
    }

    if (!token.email().equalsIgnoreCase(request.email())) {
      throw new InvalidOrExpiredTokenException();
    }

    AuthUser user =
        authUserRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException("Email ou senha inválidos!"));

    user.updatePassword(request.newPassword(), passwordEncoder);

    try {
      authUserRepository.save(user);
      tokenRepository.delete(token);
    } catch (Exception e) {
      throw new InternalErrorException("Erro ao atualizar senha");
    }

    String html = templateBuilder.buildPasswordChangedEmail();
    try {
      emailSender.sendEmail(user.getEmail(), "Senha alterada com sucesso - Orienta+", html);
    } catch (Exception ignored) {
    }
  }

  @Override
  public void changePassword(ChangePasswordModelRequest request) {
    AuthUser user =
        authUserRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException("Email ou senha inválidos!"));

    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
      throw new BadRequestException("Email ou senha inválidos!");
    }

    user.updatePassword(request.newPassword(), passwordEncoder);

    try {
      authUserRepository.save(user);
    } catch (Exception e) {
      throw new InternalErrorException("Erro ao atualizar senha");
    }

    String html = templateBuilder.buildPasswordChangedEmail();
    try {
      emailSender.sendEmail(user.getEmail(), "Senha alterada com sucesso - Orienta+", html);
    } catch (Exception ignored) {
    }
  }
}
