package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Getter
@Setter
@Entity
@Table(name = "certificate")
public class Certificate {

  @Id
  @GeneratedValue
  @Column(unique = true, nullable = false)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "mentored_id", nullable = false)
  private Mentored mentored;

  @ManyToOne
  @JoinColumn(name = "class_id", nullable = false)
  private Lesson clazz;
}
