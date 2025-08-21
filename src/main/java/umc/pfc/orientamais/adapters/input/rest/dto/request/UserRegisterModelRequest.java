package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
public class UserRegisterModelRequest {
    @NotBlank(message = "Este campo é obrigatório!")
    @Email(message = "Campo e-mail incorreto")
    private String email;

    @NotBlank(message = "Este campo é obrigatório!")
    private String name;

    @NotBlank(message = "Este campo é obrigatório!")
    private String lastName;

    @NotBlank(message = "Este campo é obrigatório!")
    @Length(min = 8, max = 50, message = "Sua senha deve conter no mínimo 8 caracteres!")
    private String password;

    @NotBlank(message = "Este campo é obrigatório!")
    private LocalDate birthDate;

    private String socialMedias;

    private String description;

    @NotBlank(message = "Este campo é obrigatório!")
    private String state;

    @NotBlank(message = "Este campo é obrigatório!")
    private String nationality;

    @NotBlank(message = "Este campo é obrigatório!")
    private String role;
}
