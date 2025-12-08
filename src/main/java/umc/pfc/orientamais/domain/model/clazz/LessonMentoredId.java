package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class LessonMentoredId implements Serializable {
  private UUID lessonId;
  private UUID mentoredId;
}
