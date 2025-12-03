package umc.pfc.orientamais.application.service.utils;

import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.model.mentor.MentorReview;

import java.util.List;

@Component
public class MentorReviewUtils {

    public MentorReviewSummary calculateSummary(List<MentorReview> reviews) {
        MentorReviewSummary summary = new MentorReviewSummary();

        if (reviews == null || reviews.isEmpty()) {
            summary.setDidactics(0.0);
            summary.setSubjectMastery(0.0);
            summary.setPunctuality(0.0);
            summary.setCommunication(0.0);
            summary.setEngagement(0.0);
            summary.setOverallFeedback(0.0);
            return summary;
        }

        int total = reviews.size();

        int didactics = 0;
        int subject = 0;
        int punctuality = 0;
        int communication = 0;
        int engagement = 0;

        for (MentorReview r : reviews) {
            didactics += r.getDidactics();
            subject += r.getSubjectMastery();
            punctuality += r.getPunctuality();
            communication += r.getCommunication();
            engagement += r.getEngagement();
        }

        summary.setDidactics((double) didactics / total);
        summary.setSubjectMastery((double) subject / total);
        summary.setPunctuality((double) punctuality / total);
        summary.setCommunication((double) communication / total);
        summary.setEngagement((double) engagement / total);

        double totalScore = didactics + subject + punctuality + communication + engagement;
        summary.setOverallFeedback(totalScore / (total * 5.0));

        return summary;
    }
}
