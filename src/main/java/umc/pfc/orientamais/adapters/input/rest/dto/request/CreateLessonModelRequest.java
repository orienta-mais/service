package umc.pfc.orientamais.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
<<<<<<<< HEAD:src/main/java/umc/pfc/orientamais/adapters/input/rest/dto/request/CreatelessonModelRequest.java
import java.time.LocalDateTime;
========
>>>>>>>> develop:src/main/java/umc/pfc/orientamais/adapters/input/rest/dto/request/CreateLessonModelRequest.java
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
<<<<<<<< HEAD:src/main/java/umc/pfc/orientamais/adapters/input/rest/dto/request/CreatelessonModelRequest.java
public class CreatelessonModelRequest {
========
public class CreateLessonModelRequest {
>>>>>>>> develop:src/main/java/umc/pfc/orientamais/adapters/input/rest/dto/request/CreateLessonModelRequest.java

    @NotBlank(message = "O título da aula é obrigatório.")
    @Size(max = 200, message = "O título da aula deve ter no máximo 200 caracteres.")
    private String title;

    @Size(max = 1000, message = "A descrição da aula deve ter no máximo 1000 caracteres.")
    private String description;

    @Size(max = 255, message = "O link deve ter no máximo 255 caracteres.")
    private String link;

    private Integer maxGuest;

    @NotNull(message = "A data da mentoria é obrigatória.")
    private LocalDate date;

    @NotNull(message = "O horário de início é obrigatório.")
    private LocalTime startTime;

    @NotNull(message = "O horário de finalização é obrigatório.")
    private LocalTime endTime;

    @NotNull(message = "O mentor é obrigatório.")
    private UUID mentorId;

    @Size(max = 100, message = "O código de presença deve ter no máximo 100 caracteres.")
    private String presentCode;
}
