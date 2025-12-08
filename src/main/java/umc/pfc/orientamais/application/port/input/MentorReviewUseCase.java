package umc.pfc.orientamais.application.port.input;

import java.util.List;
import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorReviewRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;

public interface MentorReviewUseCase {
  GenericModelResponse addMentorReview(UUID lessonId, MentorReviewRequest request);

  List<MentorReviewResponse> listMentorReviews(UUID mentorId);
}
