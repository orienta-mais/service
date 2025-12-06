package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorReviewUtilsTest {

    @InjectMocks
    private MentorReviewUtils mentorReviewUtils;

    private MentorReview review(int didactics, int subject, int punctuality, int communication, int engagement) {
        MentorReview review = new MentorReview();
        review.setDidactics(didactics);
        review.setSubjectMastery(subject);
        review.setPunctuality(punctuality);
        review.setCommunication(communication);
        review.setEngagement(engagement);
        return review;
    }

    @BeforeEach
    void setUp() {
        // No dependencies
    }

    @Test
    void calculateSummaryShouldReturnZeroesWhenListIsNull() {
        MentorReviewSummary summary = mentorReviewUtils.calculateSummary(null);

        assertEquals(0.0, summary.getDidactics());
        assertEquals(0.0, summary.getOverallFeedback());
    }

    @Test
    void calculateSummaryShouldReturnZeroesWhenListIsEmpty() {
        MentorReviewSummary summary = mentorReviewUtils.calculateSummary(List.of());

        assertEquals(0.0, summary.getCommunication());
    }

    @Test
    void calculateSummaryShouldComputeAveragesCorrectly() {
        MentorReviewSummary summary = mentorReviewUtils.calculateSummary(List.of(
                review(5, 4, 3, 2, 1),
                review(4, 4, 4, 4, 4)
        ));

        assertEquals(4.5, summary.getDidactics());
        assertEquals(4.0, summary.getSubjectMastery());
        assertEquals(3.5, summary.getPunctuality());
        assertEquals(3.0, summary.getCommunication());
        assertEquals(2.5, summary.getEngagement());
        assertEquals((5+4+3+2+1+4+4+4+4+4)/(2.0*5.0), summary.getOverallFeedback());
    }
}

