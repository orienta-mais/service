package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ClassInterestId implements Serializable {
  private UUID classId;
  private UUID interestId;
}
