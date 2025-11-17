package umc.pfc.orientamais.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

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
