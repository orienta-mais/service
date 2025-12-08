package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.validation.SafeInput;
import umc.pfc.orientamais.domain.validation.ValidAge;

public record UserRegisterModelRequest(
    @NotBlank(message = "Este campo é obrigatório!")
        @Email(message = "Campo e-mail incorreto")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        @SafeInput(
            checkFor = {
              SafeInput.InjectionType.SQL_INJECTION,
              SafeInput.InjectionType.NULL_BYTES,
              SafeInput.InjectionType.COMMAND_INJECTION
            },
            message = "Email contém caracteres suspeitos")
        String email,
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(
            regexp = "^[a-zA-ZÀ-ÿ\\s'\\-]{2,100}$",
            message = "Nome inválido. Use apenas letras, espaços, hífens e apóstrofos")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        @SafeInput(message = "Nome contém caracteres suspeitos")
        String name,
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(
            regexp = "^[a-zA-ZÀ-ÿ\\s'\\-]{2,100}$",
            message = "Sobrenome inválido. Use apenas letras, espaços, hífens e apóstrofos")
        @Size(min = 2, max = 100, message = "Sobrenome deve ter entre 2 e 100 caracteres")
        @SafeInput(message = "Sobrenome contém caracteres suspeitos")
        String lastName,
    @NotBlank(message = "Este campo é obrigatório!")
        @Length(min = 8, max = 128, message = "Sua senha deve conter entre 8 e 128 caracteres")
        @Pattern(
            regexp =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`])[A-Za-z\\d@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`]{8,128}$",
            message =
                "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
        String password,
    @NotBlank(message = "Este campo é obrigatório!")
    @Length(min = 8, max = 128, message = "Sua senha deve conter entre 8 e 128 caracteres")
    @Pattern(
      regexp =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`])[A-Za-z\\d@$!%*?&#^()_+=\\-\\[\\]{}|;:,.<>~`]{8,128}$",
      message =
        "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
    String confirmPassword,
    @NotNull(message = "Este campo é obrigatório!")
        @Past(message = "Data de nascimento deve ser no passado")
        @ValidAge(min = 16, max = 100, message = "Idade deve estar entre 16 e 100 anos")
        LocalDate birthDate,
    @Size(max = 500, message = "Redes sociais deve ter no máximo 500 caracteres")
        @Pattern(
            regexp = "^[a-zA-Z0-9À-ÿ\\s@._/:\\-,;|]*$",
            message = "Redes sociais contém caracteres inválidos")
        @SafeInput(message = "Redes sociais contém caracteres suspeitos")
        String socialMedias,
    @Size(max = 5000, message = "Descrição deve ter no máximo 5000 caracteres")
        @SafeInput(allowHtml = true, message = "Descrição contém conteúdo suspeito")
        String description,
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]{2,100}$", message = "Estado inválido")
        @Size(min = 2, max = 100, message = "Estado deve ter entre 2 e 100 caracteres")
        @SafeInput(message = "Estado contém caracteres suspeitos")
        String state,
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]{2,100}$", message = "Nacionalidade inválida")
        @Size(min = 2, max = 100, message = "Nacionalidade deve ter entre 2 e 100 caracteres")
        @SafeInput(message = "Nacionalidade contém caracteres suspeitos")
        String nationality,
    @NotBlank(message = "Este campo é obrigatório!")
        @Pattern(regexp = "^[a-zA-Z0-9\\-]{36,255}$", message = "Token inválido")
        @Size(min = 36, max = 255, message = "Token deve ter entre 36 e 255 caracteres")
        @SafeInput(message = "Token contém caracteres suspeitos")
        String token) {}
