package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountByStateResponse {
    private List<CountMentorAndMentoredByStateResponse> total;
}
