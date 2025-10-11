package umc.pfc.orientamais.adapters.input.rest.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdatelessonModelRequest {
    private String id;
    private String title;
    private String description;
    private String link;
    private Integer maxGuest;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID mentorId;
    private String presentCode;
}
