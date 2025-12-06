package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.clazz.LessonMentoredId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonMentoredRepository extends JpaRepository<LessonMentored, LessonMentoredId> {
    boolean existsByLessonIdAndMentoredId(UUID lessonId, UUID mentoredId);

    List<LessonMentored> findByMentoredId(UUID mentoredId);

    Optional<LessonMentored> findByLessonId(UUID lessonId);

    long countByLessonId(UUID lessonId);

    Optional<LessonMentored> findByLessonIdAndMentoredId(UUID lessonId, UUID mentoredId);
}
