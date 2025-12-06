package umc.pfc.orientamais.adapters.output.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.auth.AuthUser;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
  Optional<AuthUser> findByEmail(String email);

  boolean existsByEmail(String email);
}
