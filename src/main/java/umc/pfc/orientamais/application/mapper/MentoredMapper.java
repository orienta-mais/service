package umc.pfc.orientamais.application.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountMentorAndMentoredByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Component
public class MentoredMapper {

  public MentoredModelResponse entityToResponse(Mentored mentored) {
    if (mentored == null) return null;

    var response = new MentoredModelResponse();
    response.setId(mentored.getId());
    response.setName(mentored.getName());
    response.setLastName(mentored.getLastName());
    response.setEmail(mentored.getUser().getEmail());
    response.setBirthDate(mentored.getBirthDate());
    response.setSocialMedias(mentored.getSocialMedias());
    response.setDescription(mentored.getDescription());
    response.setState(mentored.getState());
    response.setNationality(mentored.getNationality());
    return response;
  }

  public List<MentoredModelResponse> entityToResponse(List<Mentored> mentoreds) {
    return mentoreds.stream().map(this::entityToResponse).collect(Collectors.toList());
  }

  public void updateEntityFromRequest(Mentored mentored, MentoredUpdateModelRequest request) {
    if (request.name() != null) mentored.setName(request.name());
    if (request.lastName() != null) mentored.setLastName(request.lastName());
    if (request.birthDate() != null) mentored.setBirthDate(request.birthDate());
    mentored.setSocialMedias(request.socialMedias());
    mentored.setDescription(request.description());
    mentored.setState(request.state());
    mentored.setNationality(request.nationality());
  }

  public CountByStateResponse toCountByStateResponse(List<Object[]> data) {
    List<CountMentorAndMentoredByStateResponse> list =
        data.stream()
            .map(
                row -> {
                  CountMentorAndMentoredByStateResponse dto =
                      new CountMentorAndMentoredByStateResponse();

                  dto.setState((String) row[0]);
                  dto.setTotalRegistered(((Number) row[1]).intValue());

                  return dto;
                })
            .toList();

    CountByStateResponse response = new CountByStateResponse();
    response.setTotal(list);

    return response;
  }
}
