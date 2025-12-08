package umc.pfc.orientamais.adapters.output.persistence.repository;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {
  Optional<Mentor> findByUserId(UUID id);

  Optional<Mentor> findById(UUID mentorId);

  @Query(value = """
            select count(*) from mentor m;
            """, nativeQuery = true)
  Integer countMentors();

  @Modifying
  @Transactional
  @Query(
      value =
          """
        UPDATE mentor
        SET birth_date   = NULL,
            social_medias = NULL,
            description   = NULL,
            state         = NULL,
            nationality   = NULL,
            active = FALSE
        WHERE id = :id
        """,
      nativeQuery = true)
  void anonymizeMentorData(UUID id);
}
