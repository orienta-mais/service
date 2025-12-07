package umc.pfc.orientamais.adapters.output.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.clazz.LessonMentored;
import umc.pfc.orientamais.domain.model.clazz.LessonMentoredId;

@Repository
public interface LessonMentoredRepository extends JpaRepository<LessonMentored, LessonMentoredId> {
  boolean existsByLessonIdAndMentoredId(UUID lessonId, UUID mentoredId);

  List<LessonMentored> findByMentoredId(UUID mentoredId);

  Optional<LessonMentored> findByLessonId(UUID lessonId);

  long countByLessonId(UUID lessonId);

    Optional<LessonMentored> findByLessonIdAndMentoredId(UUID lessonId, UUID mentoredId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from class_mentored where mentored_id = :id;
            """, nativeQuery = true)
    void deleteLessonByMentoredId(UUID id);
}
