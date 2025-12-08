package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for password reset with comprehensive security validations. */
public record ResetPasswordModelRequest(
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(regexp = "^[a-zA-Z0-9\\-]{36,255}$", message = "Token inválido")
        @Size(min = 36, max = 255, message = "Token deve ter entre 36 e 255 caracteres")
        @SafeInput(message = "Token contém caracteres suspeitos")
        String token,
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
        @Length(min = 8, max = 128, message = "A nova senha deve conter entre 8 e 128 caracteres")
        @Pattern(
            regexp =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`])[A-Za-z\\d@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`]{8,128}$",
            message =
                "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
        String newPassword) {}
