package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.*;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for mentor review with comprehensive security validations. */
public record MentorReviewRequest(
    @NotNull(message = "Didática é obrigatória")
        @Min(value = 1, message = "Didática deve ser no mínimo 1")
        @Max(value = 5, message = "Didática deve ser no máximo 5")
        Integer didactics,
    @NotNull(message = "Domínio do assunto é obrigatório")
        @Min(value = 1, message = "Domínio do assunto deve ser no mínimo 1")
        @Max(value = 5, message = "Domínio do assunto deve ser no máximo 5")
        Integer subjectMastery,
    @NotNull(message = "Pontualidade é obrigatória")
        @Min(value = 1, message = "Pontualidade deve ser no mínimo 1")
        @Max(value = 5, message = "Pontualidade deve ser no máximo 5")
        Integer punctuality,
    @NotNull(message = "Comunicação é obrigatória")
        @Min(value = 1, message = "Comunicação deve ser no mínimo 1")
        @Max(value = 5, message = "Comunicação deve ser no máximo 5")
        Integer communication,
    @NotNull(message = "Engajamento é obrigatório")
        @Min(value = 1, message = "Engajamento deve ser no mínimo 1")
        @Max(value = 5, message = "Engajamento deve ser no máximo 5")
        Integer engagement,
    @Size(max = 2000, message = "Feedback deve ter no máximo 2000 caracteres")
        @Pattern(regexp = "^[^<>{}\\\\]*$", message = "Feedback contém caracteres inválidos")
        @SafeInput(message = "Feedback contém caracteres suspeitos")
        String feedback) {}
