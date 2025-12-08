package umc.pfc.orientamais.adapters.output.persistence.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

public interface MentorReviewRepository extends JpaRepository<MentorReview, UUID> {
  List<MentorReview> findByMentorId(UUID mentorId);

  List<MentorReview> findByMentoredId(UUID mentoredId);

  @Transactional
  @Modifying
  @Query(
      value = """
                DELETE FROM mentor_review WHERE mentor_id = :id;
            """,
      nativeQuery = true)
  void anonymizeMentorReviews(UUID id);

  @Modifying
  @Transactional
  @Query(
      value = """
            delete from mentor_review where mentored_id = :id;
            """,
      nativeQuery = true)
  void deleteReviewByMentoredId(UUID id);
}
