package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.RegisterUserModelResponse;

public interface RegisterUserUseCase {
    RegisterUserModelResponse registerUser(UserRegisterModelRequest request);
}
