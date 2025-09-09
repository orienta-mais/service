package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.GenericModelResponse;

public interface RegisterUserUseCase {
    GenericModelResponse registerUser(UserRegisterModelRequest request);
}
