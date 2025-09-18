package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailModelRequest(
        @NotBlank @Email String email
) {
}
