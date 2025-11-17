package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PresenceCodeModelRequest(
        @NotBlank(message = "Este campo é obrigatório!")
        String code
) {
}