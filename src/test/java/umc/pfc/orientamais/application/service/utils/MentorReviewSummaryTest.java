package umc.pfc.orientamais.application.service.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MentorReviewSummaryTest {

  @Test
  void gettersAndSettersShouldWork() {
    MentorReviewSummary summary = new MentorReviewSummary();
    summary.setDidactics(1);
    summary.setSubjectMastery(2);
    summary.setPunctuality(3);
    summary.setCommunication(4);
    summary.setEngagement(5);
    summary.setOverallFeedback(6);

    assertEquals(1, summary.getDidactics());
    assertEquals(2, summary.getSubjectMastery());
    assertEquals(3, summary.getPunctuality());
    assertEquals(4, summary.getCommunication());
    assertEquals(5, summary.getEngagement());
    assertEquals(6, summary.getOverallFeedback());
  }
}
