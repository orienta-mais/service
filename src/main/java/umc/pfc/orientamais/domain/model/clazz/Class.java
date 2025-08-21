package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "class")
public class Class {

    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String link;

    @Column(name = "max_guest")
    private Integer maxGuest;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(name = "present_code")
    private String presentCode;

    @OneToMany(mappedBy = "clazz")
    private List<ClassInterest> interests;

    @OneToMany(mappedBy = "clazz")
    private List<ClassMentored> mentoreds;
}
