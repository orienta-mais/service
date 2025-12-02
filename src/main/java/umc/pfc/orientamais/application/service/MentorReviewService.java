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
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorReviewService implements MentorReviewUseCase {

    private final LessonRepository lessonRepository;
    private final MentoredRepository mentoredRepository;
    private final MentorRepository mentorRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final LessonMentoredRepository lessonMentoredRepository;
    private final MentorReviewMapper mapper;

    @Override
    public GenericModelResponse addMentorReview(UUID lessonId, MentorReviewRequest request) {
        UUID authProfileId = SecurityUtils.getCurrentProfileId();

        Mentored mentored = mentoredRepository.findByUserId(authProfileId)
                .orElseThrow(() -> new NotFoundException("Mentorado não encontrado"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Aula não encontrada"));

        UUID mentoredId = mentored.getId();

        if (!lessonMentoredRepository.existsByLessonIdAndMentoredId(lessonId, mentoredId)) {
            throw new BadRequestException("Você não está inscrito nesta aula");
        }

        Mentor mentor = lesson.getMentor();

        if (mentor == null) {
            throw new NotFoundException("Mentor não encontrado");
        }

        MentorReview entity = mapper.toEntity(request, mentor.getId(), mentoredId, lessonId);

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
