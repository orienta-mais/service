package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.util.UUID;

public record MentorReviewResponse(
        UUID id,
        UUID mentorId,
        UUID mentoredId,
        UUID lessonId,
        Integer didactics,
        Integer subjectMastery,
        Integer punctuality,
        Integer communication,
        Integer engagement,
        String feedback
) {
}
