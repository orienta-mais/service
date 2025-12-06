package umc.pfc.orientamais.adapters.output.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, UUID> {
  Optional<RegistrationToken> findByToken(String token);

  Optional<RegistrationToken> findByEmail(String email);

  void deleteByEmail(String email);
}
