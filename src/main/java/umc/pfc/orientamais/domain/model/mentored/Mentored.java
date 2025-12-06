package umc.pfc.orientamais.domain.model.mentored;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.Profile;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.utils.InputSanitizer;

@Getter
@Setter
@Entity
@Table(name = "mentored")
public class Mentored implements Profile {

  @Id
  @GeneratedValue
  @Column(unique = true, nullable = false)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_uuid", nullable = false, unique = true)
  private AuthUser user;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String lastName;

  private LocalDate birthDate;

  @Column(name = "social_medias")
  private String socialMedias;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String state;

  private String nationality;

  @OneToMany(mappedBy = "mentored")
  private List<MentoredInterest> interests;

  @OneToMany(mappedBy = "mentored")
  private List<LessonMentored> classes;

  @Override
  public void fillFromRequest(AuthUser user, UserRegisterModelRequest request) {
    this.user = user;
    this.name = InputSanitizer.sanitize(request.name());
    this.lastName = InputSanitizer.sanitize(request.lastName());
    this.birthDate = request.birthDate();
    this.socialMedias = InputSanitizer.sanitize(request.socialMedias());
    this.description = InputSanitizer.sanitize(request.description());
    this.state = InputSanitizer.sanitize(request.state());
    this.nationality = InputSanitizer.sanitize(request.nationality());
  }
}
