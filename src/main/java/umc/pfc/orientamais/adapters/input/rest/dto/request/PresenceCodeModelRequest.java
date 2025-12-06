package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for presence code with comprehensive security validations. */
public record PresenceCodeModelRequest(
    @NotBlank(message = "Este campo é obrigatório!")
        @Size(min = 4, max = 100, message = "Código deve ter entre 4 e 100 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9\\-_]+$", message = "Código contém caracteres inválidos")
        @SafeInput(message = "Código contém caracteres suspeitos")
        String code) {}
