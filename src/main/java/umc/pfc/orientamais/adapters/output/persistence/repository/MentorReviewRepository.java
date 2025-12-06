package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

import java.util.List;
import java.util.UUID;

public interface MentorReviewRepository extends JpaRepository<MentorReview, UUID> {
    List<MentorReview> findByMentorId(UUID mentorId);

    List<MentorReview> findByMentoredId(UUID mentoredId);
}
