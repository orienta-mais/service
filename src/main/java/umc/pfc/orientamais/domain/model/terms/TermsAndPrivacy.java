package umc.pfc.orientamais.domain.model.terms;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "terms_and_privacy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndPrivacy {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", nullable = false, updatable = false)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TermType type;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private Integer version = 1;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  public TermsAndPrivacy(TermType type, String content) {
    this.type = type;
    this.content = content;
    this.version = 1;
    this.isActive = true;
  }

  public TermsAndPrivacy(TermType type, String content, Integer version) {
    this.type = type;
    this.content = content;
    this.version = version;
    this.isActive = true;
  }
}
