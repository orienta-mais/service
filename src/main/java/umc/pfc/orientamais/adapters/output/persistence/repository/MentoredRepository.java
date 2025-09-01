package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.UUID;

public interface MentoredRepository extends JpaRepository<Mentored, UUID> {
}
