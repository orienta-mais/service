package umc.pfc.orientamais.adapters.output.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.adapters.output.persistence.entity.PasswordResetTokenEntity;

@Repository
public interface SpringDataPasswordResetTokenRepository
    extends JpaRepository<PasswordResetTokenEntity, String> {
  Optional<PasswordResetTokenEntity> findByEmail(String email);

  void deleteByEmail(String email);
}
