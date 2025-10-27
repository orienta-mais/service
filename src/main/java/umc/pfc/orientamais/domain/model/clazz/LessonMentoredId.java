package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class LessonMentoredId implements Serializable {
    private UUID lessonId;
    private UUID mentoredId;
}
