package umc.pfc.orientamais.domain.model.clazz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.pfc.orientamais.domain.model.Interest;

@Getter
@Setter
@Entity
@Table(name = "class_interests")
public class ClassInterest {

    @EmbeddedId
    private ClassInterestId id;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    private Lesson clazz;

    @ManyToOne
    @MapsId("interestId")
    @JoinColumn(name = "interest_id")
    private Interest interest;
}
