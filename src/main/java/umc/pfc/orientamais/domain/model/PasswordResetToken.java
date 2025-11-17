package umc.pfc.orientamais.domain.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

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
