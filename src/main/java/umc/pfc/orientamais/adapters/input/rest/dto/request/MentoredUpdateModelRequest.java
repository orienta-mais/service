package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MentoredUpdateModelRequest(
        @NotBlank(message = "Este campo é obrigatório!")
        String name,

        @NotBlank(message = "Este campo é obrigatório!")
        String lastName,

        @NotNull(message = "Este campo é obrigatório!")
        LocalDate birthDate,

        String socialMedias,

        String description,

        @NotBlank(message = "Este campo é obrigatório!")
        String state,

        @NotBlank(message = "Este campo é obrigatório!")
        String nationality
) {
}
