package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.application.mapper.MentorMapper;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorService implements MentorUseCase {

    private final MentorRepository mentorRepository;
    private final MentorMapper mentorMapper;
    private final AuthUserRepository authUserRepository;

    @Override
    public List<MentorModelResponse> getAllMentors() {
        return mentorMapper.entityToResponse(mentorRepository.findAll());
    }

    @Override
    public MentorModelResponse getMentorById(UUID id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        return mentorMapper.entityToResponse(mentor);
    }

    @Override
    @Transactional
    public MentorModelResponse updateMentor(UUID id, MentorUpdateModelRequest request) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        checkPermission(mentor);
        mentorMapper.updateEntityFromRequest(mentor, request);
        return mentorMapper.entityToResponse(mentorRepository.save(mentor));
    }

    @Override
    @Transactional
    public void deleteMentor(UUID id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        checkPermission(mentor);
        AuthUser authUser = mentor.getUser();
        mentorRepository.delete(mentor);
        authUserRepository.delete(authUser);
    }

    private void checkPermission(Mentor mentor) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = mentor.getUser().getEmail().equalsIgnoreCase(authUser.getEmail());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Você não tem permissão para executar esta ação.");
        }
    }
}