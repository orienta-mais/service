package umc.pfc.orientamais.application.port.input;

import java.util.List;
import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.CountByStateResponse;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;

public interface MentoredUseCase {
  List<MentoredModelResponse> getAllMentoreds();

  MentoredModelResponse getMentoredById(UUID id);

  MentoredModelResponse updateMentored(UUID id, MentoredUpdateModelRequest request);

  void deleteMentored(UUID id);

  Integer countMentoreds();

  CountByStateResponse countMentoredsByState();

  void deleteMentoredCascade(UUID id);
}
