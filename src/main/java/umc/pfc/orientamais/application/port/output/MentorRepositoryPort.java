package umc.pfc.orientamais.application.port.output;

import umc.pfc.orientamais.domain.model.mentor.Mentor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorRepositoryPort {
    Mentor save(Mentor mentor);
    Optional<Mentor> findById(UUID id);
    List<Mentor> findAll();
    void delete(Mentor mentor);
}
