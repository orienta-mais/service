package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordModelRequest(
        @NotBlank String token,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String newPassword
) {
}
