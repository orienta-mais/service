package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonModelResponse {
  private UUID id;
  private String title;
  private String description;
  private Integer maxGuest;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private String mentorName;
}
