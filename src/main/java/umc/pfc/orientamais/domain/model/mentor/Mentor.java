package umc.pfc.orientamais.domain.model.mentor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.clazz.Class;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "mentor")
public class Mentor {

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

    @OneToMany(mappedBy = "mentor")
    private List<Class> classes;

    @OneToMany(mappedBy = "mentor")
    private List<MentorInterest> interests;
}
