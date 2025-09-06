package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LeasonModelResponse {
    private UUID id;
    private String title;
    private String description;
    private String link;
    private Integer maxGuest;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String presentCode;
}
