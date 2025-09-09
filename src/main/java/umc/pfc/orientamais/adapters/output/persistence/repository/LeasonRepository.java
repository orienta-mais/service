package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.Leason;

import java.util.UUID;

@Repository
public interface LeasonRepository extends JpaRepository<Leason, UUID> {
}
