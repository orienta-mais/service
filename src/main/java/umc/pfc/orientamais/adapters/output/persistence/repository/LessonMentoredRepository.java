package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.clazz.LessonMentoredId;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonMentoredRepository extends JpaRepository<LessonMentored, LessonMentoredId> {
    boolean existsByLessonIdAndMentoredId(UUID lessonId, UUID mentoredId);

    List<LessonMentored> findByMentoredId(UUID mentoredId);

    long countByLessonId(UUID lessonId);
}
