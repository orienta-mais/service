package umc.pfc.orientamais.domain.model.mentor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mentor_review")
@Getter
@Setter
public class MentorReview {

  @Id
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "mentor_id", nullable = false)
  private UUID mentorId;

  @Column(name = "mentored_id", nullable = false)
  private UUID mentoredId;

  @Column(nullable = false)
  private Integer didactics;

  @Column(name = "subject_mastery", nullable = false)
  private Integer subjectMastery;

  @Column(nullable = false)
  private Integer punctuality;

  @Column(nullable = false)
  private Integer communication;

  @Column(nullable = false)
  private Integer engagement;

  @Column(length = 500)
  private String feedback;
}
