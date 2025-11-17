package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MentorReviewRequest(
        @Min(1) @Max(5) Integer didactics,
        @Min(1) @Max(5) Integer subjectMastery,
        @Min(1) @Max(5) Integer punctuality,
        @Min(1) @Max(5) Integer communication,
        @Min(1) @Max(5) Integer engagement,
        @Size(max = 500) String feedback
) {
}
