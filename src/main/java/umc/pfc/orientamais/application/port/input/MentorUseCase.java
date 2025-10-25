package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.MentorModelResponse;

import java.util.List;
import java.util.UUID;

public interface MentorUseCase {
    List<MentorModelResponse> getAllMentors();

    MentorModelResponse getMentorById(UUID id);

    MentorModelResponse updateMentor(UUID id, MentorUpdateModelRequest request);

    void deleteMentor(UUID id);
}
