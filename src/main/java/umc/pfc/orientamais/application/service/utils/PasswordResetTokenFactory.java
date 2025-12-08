package umc.pfc.orientamais.application.service.utils;

import java.time.Instant;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

@Component
public class PasswordResetTokenFactory {

  public PasswordResetToken create(String email, long ttlSeconds) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(ttlSeconds);
    return PasswordResetToken.builder()
        .token(null)
        .email(email)
        .createdAt(now)
        .expiresAt(expiresAt)
        .build();
  }
}
