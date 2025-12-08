package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonDetailsModelResponse {
  private UUID id;
  private String title;
  private String description;
  private String link;
  private Integer maxGuest;
  private String presentCode;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private UUID mentorId;
  private String mentorName;
  private List<String> additionalLinks;
  private Boolean presentCodeFilled;
}
