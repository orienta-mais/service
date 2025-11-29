package umc.pfc.orientamais.application.mapper;

import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

import java.util.UUID;

@Component
public class MentorReviewMapper {

    public MentorReview toEntity(MentorReviewRequest request, UUID mentorId, UUID mentoredId, UUID lessonId) {
        MentorReview entity = new MentorReview();
        entity.setId(UUID.randomUUID());
        entity.setMentorId(mentorId);
        entity.setMentoredId(mentoredId);
        entity.setLessonId(lessonId);
        entity.setDidactics(request.didactics());
        entity.setSubjectMastery(request.subjectMastery());
        entity.setPunctuality(request.punctuality());
        entity.setCommunication(request.communication());
        entity.setEngagement(request.engagement());
        entity.setFeedback(request.feedback());
        return entity;
    }

    public MentorReviewResponse toResponse(MentorReview entity) {
        return new MentorReviewResponse(
                entity.getId(),
                entity.getMentorId(),
                entity.getMentoredId(),
                entity.getLessonId(),
                entity.getDidactics(),
                entity.getSubjectMastery(),
                entity.getPunctuality(),
                entity.getCommunication(),
                entity.getEngagement(),
                entity.getFeedback()
        );
    }
}
