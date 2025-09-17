package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

public interface RegisterMentorUseCase {
    void register(UserRegisterModelRequest request);
}
