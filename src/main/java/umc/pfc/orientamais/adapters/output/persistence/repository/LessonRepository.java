package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.pfc.orientamais.domain.model.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {
    List<Lesson> findByMentorId(UUID mentorId);

    @Query(value = """
    SELECT * FROM class c
    WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:startDate IS NULL OR DATE(c.start_time) = :startDate)
""", nativeQuery = true)
    List<Lesson> findAllLessonsByFilters(
            @Param("title") String title,
            @Param("startDate") LocalDate startDate
    );

    @Query(value = """
            select count(*) from "class" c
            where c.start_time > NOW();
            """, nativeQuery = true)
    Integer countUpcomingLessons();

    @Query(value = """
            SELECT COUNT(*) AS total_classes_disponiveis
            FROM (
                SELECT
                    c.id,
                    c.max_guest,
                    COUNT(cm.*) AS total_mentored
                FROM "class" c
                LEFT JOIN class_mentored cm ON cm.class_id = c.id
                WHERE c.end_time < NOW()
                GROUP BY c.id, c.max_guest
                HAVING COUNT(cm.*) <= c.max_guest
            ) AS sub;
            """, nativeQuery = true)
    Integer countUnavailableLessons();
}
