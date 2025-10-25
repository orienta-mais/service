package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.MentoredUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentoredModelResponse;

import java.util.List;
import java.util.UUID;

public interface MentoredUseCase {
    List<MentoredModelResponse> getAllMentoreds();

    MentoredModelResponse getMentoredById(UUID id);

    MentoredModelResponse updateMentored(UUID id, MentoredUpdateModelRequest request);

    void deleteMentored(UUID id);
}