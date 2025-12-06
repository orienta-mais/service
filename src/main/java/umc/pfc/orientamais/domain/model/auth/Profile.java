package umc.pfc.orientamais.domain.model.auth;

import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

import java.util.UUID;

public interface Profile {
    UUID getId();
    String getName();
    void fillFromRequest(AuthUser user, UserRegisterModelRequest request);
}
