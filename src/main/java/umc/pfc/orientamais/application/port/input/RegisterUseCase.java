package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

public interface RegisterUseCase {
    void register(UserRegisterModelRequest request, AuthUserRole role);
}
