package umc.pfc.orientamais.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "class")
public class Lesson {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @UuidGenerator
    private UUID id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String link;

    @Column(name = "max_guest")
    private Integer maxGuest;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_class_mentor"))
    private Mentor mentor;

    @Column(name = "present_code", length = 100)
    private String presentCode;

    @Column(name = "external_event_id", length = 255)
    private String externalEventId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
