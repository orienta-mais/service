package umc.pfc.orientamais.adapters.input.rest.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorInfoModelResponse {
  private UUID id;
  private String name;
  private String state;
  private String nationality;
  private String socialMedias;
  private String description;
  private Integer totalClasses;
  private Boolean canAddReview;
  private List<MentorReviewResponse> reviews;
}
