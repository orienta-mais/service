package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for listing lessons with comprehensive security validations. */
@Getter
@Setter
public class ListLessonsRequest {

  @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
  @Pattern(
      regexp = "^[a-zA-Z0-9À-ÿ\\s\\-.,!?():]*$",
      message = "Título contém caracteres inválidos")
  @SafeInput(message = "Título contém caracteres suspeitos")
  private String title;

  private LocalDate date;
}
