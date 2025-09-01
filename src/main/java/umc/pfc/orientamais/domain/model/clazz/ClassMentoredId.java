package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ClassMentoredId implements Serializable {
    private UUID classId;
    private UUID mentoredId;
}
