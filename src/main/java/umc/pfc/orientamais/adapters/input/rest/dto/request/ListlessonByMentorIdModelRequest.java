package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for listing lessons by mentor ID with comprehensive security validations. */
@Getter
public class ListlessonByMentorIdModelRequest {

  @NotBlank(message = "O ID do mentor é obrigatório.")
  @Pattern(
      regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$",
      message = "ID inválido. Deve ser um UUID válido")
  @Size(min = 36, max = 36, message = "ID deve ter 36 caracteres")
  @SafeInput(message = "ID contém caracteres suspeitos")
  private String mentorId;
}
