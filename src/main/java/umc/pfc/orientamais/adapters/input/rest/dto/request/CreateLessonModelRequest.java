package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.validation.SafeInput;

/** Request model for lesson creation with comprehensive security validations. */
@Getter
@Setter
public class CreateLessonModelRequest {

  @NotBlank(message = "O título da aula é obrigatório.")
  @Size(min = 3, max = 200, message = "O título da aula deve ter entre 3 e 200 caracteres.")
  @Pattern(
      regexp = "^[a-zA-Z0-9À-ÿ\\s\\-.,!?():]+$",
      message = "Título contém caracteres inválidos")
  @SafeInput(message = "Título contém caracteres suspeitos")
  private String title;

  @Size(max = 5000, message = "A descrição da aula deve ter no máximo 5000 caracteres.")
  @Pattern(regexp = "^[^<>{}\\\\]*$", message = "Descrição contém caracteres inválidos")
  @SafeInput(message = "Descrição contém caracteres suspeitos")
  private String description;

  @Min(value = 1, message = "Número máximo de participantes deve ser no mínimo 1")
  @Max(value = 1000, message = "Número máximo de participantes deve ser no máximo 1000")
  private Integer maxGuest;

  @NotNull(message = "A data da mentoria é obrigatória.")
  @Future(message = "A data da mentoria deve ser no futuro")
  private LocalDate date;

  @NotNull(message = "O horário de início é obrigatório.")
  private LocalTime startTime;

  @NotNull(message = "O horário de finalização é obrigatório.")
  private LocalTime endTime;

  @NotNull(message = "O mentor é obrigatório.")
  private UUID mentorId;

  @Size(min = 4, max = 100, message = "O código de presença deve ter entre 4 e 100 caracteres.")
  @Pattern(
      regexp = "^[a-zA-Z0-9\\-_]+$",
      message = "Código de presença contém caracteres inválidos")
  @SafeInput(message = "Código de presença contém caracteres suspeitos")
  private String presentCode;

  @Size(max = 10, message = "Máximo de 10 links adicionais permitidos")
  private List<
          @Pattern(
              regexp = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}[^\\s<>]*$",
              message = "URL inválida")
          String>
      additionalLinks;
}
