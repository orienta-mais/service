package umc.pfc.orientamais.domain.model.mentor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.Interest;

@Getter
@Setter
@Entity
@Table(name = "mentor_interests")
public class MentorInterest {

  @EmbeddedId private MentorInterestId id;

  @ManyToOne
  @MapsId("mentorId")
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @ManyToOne
  @MapsId("interestId")
  @JoinColumn(name = "interest_id")
  private Interest interest;
}
