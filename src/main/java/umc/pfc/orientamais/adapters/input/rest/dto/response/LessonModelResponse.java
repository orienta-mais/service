package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class LessonModelResponse {
    private UUID id;
    private String title;
    private String description;
    private String link;
    private Integer maxGuest;
    private String presentCode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mentorName;
    private Boolean presentCodeFilled;
}
