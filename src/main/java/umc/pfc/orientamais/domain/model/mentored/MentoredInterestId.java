package umc.pfc.orientamais.domain.model.mentored;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class MentoredInterestId implements Serializable {
    private UUID mentoredId;
    private UUID interestId;
}
