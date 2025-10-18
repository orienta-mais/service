package umc.pfc.orientamais.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.application.port.output.MentorRepositoryPort;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MentorRepositoryAdapter implements MentorRepositoryPort {

    private final MentorRepository mentorRepository;

    @Override
    public Mentor save(Mentor mentor) {
        return mentorRepository.save(mentor);
    }

    @Override
    public Optional<Mentor> findById(UUID id) {
        return mentorRepository.findById(id);
    }

    @Override
    public List<Mentor> findAll() {
        return mentorRepository.findAll();
    }

    @Override
    public void delete(Mentor mentor) {
        mentorRepository.delete(mentor);
    }
}
