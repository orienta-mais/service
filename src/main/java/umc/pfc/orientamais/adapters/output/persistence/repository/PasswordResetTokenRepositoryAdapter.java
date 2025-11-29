package umc.pfc.orientamais.adapters.output.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.output.persistence.entity.PasswordResetTokenEntity;
import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenRepository jpa;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .token(token.token())
                .email(token.email())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .build();
        PasswordResetTokenEntity saved = jpa.save(entity);
        return PasswordResetToken.builder()
                .token(saved.getToken())
                .email(saved.getEmail())
                .expiresAt(saved.getExpiresAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpa.findById(token)
                .map(e -> PasswordResetToken.builder()
                        .token(e.getToken())
                        .email(e.getEmail())
                        .expiresAt(e.getExpiresAt())
                        .createdAt(e.getCreatedAt())
                        .build());
    }

    @Override
    public Optional<PasswordResetToken> findByEmail(String email) {
        return jpa.findByEmail(email)
                .map(e -> PasswordResetToken.builder()
                        .token(e.getToken())
                        .email(e.getEmail())
                        .expiresAt(e.getExpiresAt())
                        .createdAt(e.getCreatedAt())
                        .build());
    }

    @Override
    public void delete(PasswordResetToken token) {
        jpa.deleteById(token.token());
    }

    @Override
    public void deleteByEmail(String email) {
        jpa.deleteByEmail(email);
    }
}
