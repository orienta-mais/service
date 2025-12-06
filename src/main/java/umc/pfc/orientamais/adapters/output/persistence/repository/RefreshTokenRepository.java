package umc.pfc.orientamais.adapters.output.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(AuthUser user);
}
