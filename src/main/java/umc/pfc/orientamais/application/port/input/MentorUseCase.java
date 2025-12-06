package umc.pfc.orientamais.application.port.input;

import java.util.List;
import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorInfoModelResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;

public interface MentorUseCase {
  List<MentorModelResponse> getAllMentors();

  MentorModelResponse getMentorById(UUID id);

  MentorInfoModelResponse getMentorInfosById(UUID id);

  MentorModelResponse updateMentor(UUID id, MentorUpdateModelRequest request);

  void deleteMentor(UUID id);

  Integer countMentors();
}
