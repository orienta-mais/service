package umc.pfc.orientamais.domain.model.mentored;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class MentoredInterestId implements Serializable {
  private UUID mentoredId;
  private UUID interestId;
}
