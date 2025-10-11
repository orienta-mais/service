package umc.pfc.orientamais.domain.model;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

public interface Profile {
    void fillFromRequest(AuthUser user, UserRegisterModelRequest request);
}
