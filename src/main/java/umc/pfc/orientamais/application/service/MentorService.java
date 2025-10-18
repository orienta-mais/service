package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.port.output.MentorRepositoryPort;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorService implements MentorUseCase {

    private final MentorRepositoryPort mentorRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Mentor> getAllMentors() {
        return mentorRepositoryPort.findAll();
    }

    @Override
    public Mentor getMentorById(UUID id) {
        return mentorRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Mentor não encontrado"));
    }

    @Override
    @Transactional
    public Mentor updateMentor(UUID id, MentorUpdateModelRequest request) {
        Mentor mentor = getMentorById(id);
        mentor.updateFromRequest(request, passwordEncoder);
        return mentorRepositoryPort.save(mentor);
    }

    @Override
    @Transactional
    public void deleteMentor(UUID id) {
        Mentor mentor = getMentorById(id);
        mentorRepositoryPort.delete(mentor);
    }
}