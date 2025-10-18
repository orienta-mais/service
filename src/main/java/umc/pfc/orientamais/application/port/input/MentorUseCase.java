package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.MentorUpdateModelRequest;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

import java.util.List;
import java.util.UUID;

public interface MentorUseCase {
    List<Mentor> getAllMentors();
    Mentor getMentorById(UUID id);
    Mentor updateMentor(UUID id, MentorUpdateModelRequest request);
    void deleteMentor(UUID id);
}
