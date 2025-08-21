package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Getter
@Setter
@Entity
@Table(name = "class_mentored")
public class ClassMentored {

    @EmbeddedId
    private ClassMentoredId id;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    private Class clazz;

    @ManyToOne
    @MapsId("mentoredId")
    @JoinColumn(name = "mentored_id")
    private Mentored mentored;
}