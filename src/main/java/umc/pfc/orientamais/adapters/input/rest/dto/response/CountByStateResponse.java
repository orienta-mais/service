package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountByStateResponse {
  private List<CountMentorAndMentoredByStateResponse> total;
}
