package umc.pfc.orientamais.domain.model.mentored;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.Interest;

@Getter
@Setter
@Entity
@Table(name = "mentored_interests")
public class MentoredInterest {

  @EmbeddedId private MentoredInterestId id;

  @ManyToOne
  @MapsId("mentoredId")
  @JoinColumn(name = "mentored_id")
  private Mentored mentored;

  @ManyToOne
  @MapsId("interestId")
  @JoinColumn(name = "interest_id")
  private Interest interest;
}
