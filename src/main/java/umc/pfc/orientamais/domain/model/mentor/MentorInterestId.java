package umc.pfc.orientamais.domain.model.mentor;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class MentorInterestId implements Serializable {
    private UUID mentorId;
    private UUID interestId;
}
