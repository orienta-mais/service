package umc.pfc.orientamais.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "interests")
public class Interest {

  @Id
  @GeneratedValue
  @Column(unique = true, nullable = false)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String name;
}
