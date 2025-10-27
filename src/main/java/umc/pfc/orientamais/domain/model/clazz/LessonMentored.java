package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.Lesson;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Getter
@Setter
@Entity
@Table(name = "class_mentored")
public class LessonMentored {

    @EmbeddedId
    private LessonMentoredId id;

    @ManyToOne
    @MapsId("lessonId")
    @JoinColumn(name = "class_id")
    private Lesson lesson;

    @ManyToOne
    @MapsId("mentoredId")
    @JoinColumn(name = "mentored_id")
    private Mentored mentored;
}