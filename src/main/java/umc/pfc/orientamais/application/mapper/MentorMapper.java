package umc.pfc.orientamais.application.mapper;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorInfoModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorReviewResponse;
import umc.pfc.orientamais.domain.model.mentor.Mentor;

@Component
@RequiredArgsConstructor
public class MentorMapper {

  public MentorModelResponse entityToResponse(Mentor mentor) {
    if (mentor == null) return null;

    var response = new MentorModelResponse();
    response.setId(mentor.getId());
    response.setName(mentor.getName());
    response.setLastName(mentor.getLastName());
    response.setEmail(mentor.getUser().getEmail());
    response.setBirthDate(mentor.getBirthDate());
    response.setSocialMedias(mentor.getSocialMedias());
    response.setDescription(mentor.getDescription());
    response.setState(mentor.getState());
    response.setNationality(mentor.getNationality());
    response.setTotalClasses(mentor.getClasses() == null ? 0 : mentor.getClasses().size());
    return response;
  }

  public List<MentorModelResponse> entityToResponse(List<Mentor> mentors) {
    return mentors.stream().map(this::entityToResponse).collect(Collectors.toList());
  }

  public void updateEntityFromRequest(Mentor mentor, MentorUpdateModelRequest request) {
    if (request.name() != null) mentor.setName(request.name());
    if (request.lastName() != null) mentor.setLastName(request.lastName());
    if (request.birthDate() != null) mentor.setBirthDate(request.birthDate());
    mentor.setSocialMedias(request.socialMedias());
    mentor.setDescription(request.description());
    mentor.setState(request.state());
    mentor.setNationality(request.nationality());
  }

  public MentorInfoModelResponse entityToInfoResponse(
      Mentor mentor, int totalClasses, boolean canAddReview, List<MentorReviewResponse> reviews) {
    var response = new MentorInfoModelResponse();
    response.setId(mentor.getId());
    response.setName(mentor.getName() + " " + mentor.getLastName());
    response.setState(mentor.getState());
    response.setNationality(mentor.getNationality());
    response.setSocialMedias(mentor.getSocialMedias());
    response.setDescription(mentor.getDescription());
    response.setTotalClasses(totalClasses);
    response.setCanAddReview(canAddReview);
    response.setReviews(reviews);
    return response;
  }
}
