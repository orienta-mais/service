package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.util.UUID;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {
}