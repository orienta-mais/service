package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UserRegisterModelRequest(
        @NotBlank(message = "Este campo é obrigatório!")
        @Email(message = "Campo e-mail incorreto")
        String email,

        @NotBlank(message = "Este campo é obrigatório!")
        String name,

        @NotBlank(message = "Este campo é obrigatório!")
        String lastName,

        @NotBlank(message = "Este campo é obrigatório!")
        @Length(min = 8, max = 50, message = "Sua senha deve conter no mínimo 8 caracteres!")
        String password,

        @NotNull(message = "Este campo é obrigatório!")
        LocalDate birthDate,

        String socialMedias,

        String description,

        @NotBlank(message = "Este campo é obrigatório!")
        String state,

        @NotBlank(message = "Este campo é obrigatório!")
        String nationality,

        @NotBlank(message = "Este campo é obrigatório!")
        String role,

        @NotBlank(message = "Este campo é obrigatório!")
        String token
) {
}

