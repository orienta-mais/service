package umc.pfc.orientamais.domain.model.auth;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

public record PasswordResetToken(String token, String email, Instant expiresAt, Instant createdAt) {

  @Builder
  public PasswordResetToken(String token, String email, Instant expiresAt, Instant createdAt) {
    this.token = token == null ? UUID.randomUUID().toString() : token;
    this.email = email;
    this.expiresAt = expiresAt;
    this.createdAt = createdAt;
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }
}
