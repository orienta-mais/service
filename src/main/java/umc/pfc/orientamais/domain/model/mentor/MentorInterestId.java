package umc.pfc.orientamais.domain.model.mentor;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class MentorInterestId implements Serializable {
  private UUID mentorId;
  private UUID interestId;
}
