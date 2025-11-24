package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountMentorAndMentoredByStateResponse {
    private String state;
    private Integer totalRegistered;
}
