package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.*;
import umc.pfc.orientamais.domain.validation.SafeInput;
import umc.pfc.orientamais.domain.validation.ValidAge;

import java.time.LocalDate;

public record MentorUpdateModelRequest(
  @NotBlank(message = "Este campo é obrigatório!")
  @Pattern(
    regexp = "^[a-zA-ZÀ-ÿ\\s'-]{2,100}$",
    message = "Nome inválido. Use apenas letras, espaços, hífens e apóstrofos")
  @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
  @SafeInput(message = "Nome contém caracteres suspeitos")
  String name,
  @NotBlank(message = "Este campo é obrigatório!")
  @Pattern(
    regexp = "^[a-zA-ZÀ-ÿ\\s'-]{2,100}$",
    message = "Sobrenome inválido. Use apenas letras, espaços, hífens e apóstrofos")
  @Size(min = 2, max = 100, message = "Sobrenome deve ter entre 2 e 100 caracteres")
  @SafeInput(message = "Sobrenome contém caracteres suspeitos")
  String lastName,
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
  @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s-]{2,100}$", message = "Estado inválido")
  @Size(min = 2, max = 100, message = "Estado deve ter entre 2 e 100 caracteres")
  @SafeInput(message = "Estado contém caracteres suspeitos")
  String state,
  @NotBlank(message = "Este campo é obrigatório!")
  @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s-]{2,100}$", message = "Nacionalidade inválida")
  @Size(min = 2, max = 100, message = "Nacionalidade deve ter entre 2 e 100 caracteres")
  @SafeInput(message = "Nacionalidade contém caracteres suspeitos")
  String nationality) {
}
