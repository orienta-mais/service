package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.*;
import umc.pfc.orientamais.application.mapper.MentorReviewMapper;
import umc.pfc.orientamais.application.port.input.MentorReviewUseCase;
import umc.pfc.orientamais.application.service.utils.SecurityUtils;
import umc.pfc.orientamais.domain.exceptions.BadRequestException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorReviewService implements MentorReviewUseCase {

    private final MentoredRepository mentoredRepository;
    private final MentorRepository mentorRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final MentorReviewMapper mapper;

    @Override
    public GenericModelResponse addMentorReview(UUID mentorId, MentorReviewRequest request) {
        UUID authProfileId = SecurityUtils.getCurrentProfileId();

        Mentored mentored = mentoredRepository.findByUserId(authProfileId)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new NotFoundException("Mentor não encontrado"));

        mentorReviewRepository.findByMentoredId(mentored.getId()).stream()
                .filter(review -> review.getMentorId().equals(mentorId))
                .findFirst()
                .ifPresent(review -> {
                    throw new BadRequestException("Você já avaliou este mentor");
                });

        MentorReview entity = mapper.toEntity(request, mentor.getId(), mentored.getId());

        mentorReviewRepository.save(entity);

        return new GenericModelResponse("MENTOR_REVIEW_CREATED", "Avaliação registrada com sucesso");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorReviewResponse> listMentorReviews(UUID mentorId) {
        if (!mentorRepository.existsById(mentorId)) {
            throw new NotFoundException("Mentor não encontrado");
        }

        return mentorReviewRepository.findByMentorId(mentorId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
