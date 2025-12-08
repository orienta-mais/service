package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for login with comprehensive security validations. */
public record LoginRequest(
    @NotBlank(message = "Este campo é obrigatório!")
        @Email(
            message = "Campo e-mail incorreto",
            regexp =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        @SafeInput(
            checkFor = {
              SafeInput.InjectionType.SQL_INJECTION,
              SafeInput.InjectionType.NULL_BYTES,
              SafeInput.InjectionType.COMMAND_INJECTION
            },
            message = "E-mail contém caracteres suspeitos")
        String email,
    @NotBlank(message = "Este campo é obrigatório!")
        @Length(min = 8, max = 128, message = "Senha deve ter entre 8 e 128 caracteres")
        String password) {}
