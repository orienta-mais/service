package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LessonModelResponse {
    private UUID id;
    private String title;
    private String description;
    private String link;
    private Integer maxGuest;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String presentCode;
}
