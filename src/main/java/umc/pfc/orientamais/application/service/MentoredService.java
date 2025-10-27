package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.application.port.input.MentoredUseCase;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentoredService implements MentoredUseCase {

    private final MentoredRepository mentoredRepository;
    private final MentoredMapper mentoredMapper;
    private final AuthUserRepository authUserRepository;

    @Override
    public List<MentoredModelResponse> getAllMentoreds() {
        return mentoredMapper.entityToResponse(mentoredRepository.findAll());
    }

    @Override
    public MentoredModelResponse getMentoredById(UUID id) {
        Mentored mentored = mentoredRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));
        return mentoredMapper.entityToResponse(mentored);
    }

    @Override
    @Transactional
    public MentoredModelResponse updateMentored(UUID id, MentoredUpdateModelRequest request) {
        Mentored mentored = mentoredRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));
        checkPermission(mentored);
        mentoredMapper.updateEntityFromRequest(mentored, request);
        return mentoredMapper.entityToResponse(mentoredRepository.save(mentored));
    }

    @Override
    @Transactional
    public void deleteMentored(UUID id) {
        Mentored mentored = mentoredRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));
        checkPermission(mentored);
        AuthUser authUser = mentored.getUser();
        mentoredRepository.delete(mentored);
        authUserRepository.delete(authUser);
    }

    private void checkPermission(Mentored mentored) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = mentored.getUser().getEmail().equalsIgnoreCase(authUser.getEmail());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Você não tem permissão para executar esta ação.");
        }
    }
}
