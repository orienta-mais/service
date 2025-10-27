package umc.pfc.orientamais.domain.model.mentored;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.Profile;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        this.name = request.name();
        this.lastName = request.lastName();
        this.birthDate = request.birthDate();
        this.socialMedias = request.socialMedias();
        this.description = request.description();
        this.state = request.state();
        this.nationality = request.nationality();
    }
}
