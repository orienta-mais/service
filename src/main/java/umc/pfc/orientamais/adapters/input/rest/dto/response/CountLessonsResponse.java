package umc.pfc.orientamais.adapters.input.rest.dto.response;

import lombok.*;

@Getter
@Setter
public class CountLessonsResponse {
  private Integer countUpcomingLessons;
  private Integer countUnavailableLessons;
}
