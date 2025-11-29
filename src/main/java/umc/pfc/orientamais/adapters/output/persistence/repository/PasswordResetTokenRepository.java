package umc.pfc.orientamais.adapters.output.persistence.repository;

import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);

    void delete(PasswordResetToken token);

    void deleteByEmail(String email);
}
