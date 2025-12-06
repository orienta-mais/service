package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorInfoModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.*;
import umc.pfc.orientamais.application.mapper.MentorMapper;
import umc.pfc.orientamais.application.mapper.MentorReviewMapper;
import umc.pfc.orientamais.application.port.input.MentorUseCase;
import umc.pfc.orientamais.application.service.utils.MentorReviewUtils;
import umc.pfc.orientamais.application.service.utils.SecurityUtils;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorService implements MentorUseCase {

    private final MentorRepository mentorRepository;
    private final MentorMapper mentorMapper;
    private final MentorReviewMapper mentorReviewMapper;
    private final AuthUserRepository authUserRepository;
    private final MentoredRepository mentoredRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final MentorReviewUtils mentorReviewUtils;
    private final LessonRepository lessonRepository;

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
    public MentorInfoModelResponse getMentorInfosById(UUID mentorId) {
        UUID mentoredAuthUserUUID = SecurityUtils.getCurrentProfileId();
        Mentored mentored = mentoredRepository.findByUserId(mentoredAuthUserUUID)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        List<MentorReview> reviews = mentorReviewRepository.findByMentorId(mentorId);

        boolean hasReviewed = reviews.stream()
                .anyMatch(review -> review.getMentoredId().equals(mentored.getId()));

        // Convert reviews to DTOs with mentored names
        List<MentorReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    Mentored reviewMentored = mentoredRepository.findById(review.getMentoredId())
                            .orElse(null);
                    String mentoredName = reviewMentored != null ?
                            reviewMentored.getName() + " " + reviewMentored.getLastName() :
                            "Usuário não encontrado";
                    return mentorReviewMapper.toResponse(review, mentoredName);
                })
                .toList();

        int totalClasses = lessonRepository.findByMentorId(mentorId).size();
        return mentorMapper.entityToInfoResponse(mentor, totalClasses, !hasReviewed, reviewResponses);
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

    @Override
    public Integer countMentors() {
        return mentorRepository.countMentors();
    }

    @Override
    public void anonymizeMentorData(UUID id) {
        var mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));
        mentorRepository.anonymizeMentorData(id);
        mentorReviewRepository.deleteMentorReviews(id);
        authUserRepository.anonymizeAuthUserData(mentor.getUser().getId());
    }
}