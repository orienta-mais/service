package umc.pfc.orientamais.application.service.register.strategy;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.domain.model.AuthUser;

public interface RoleRegistrationStrategy {
    boolean supports(String role);
    void register(UserRegisterModelRequest request, AuthUser user);
}
