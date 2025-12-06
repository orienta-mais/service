package umc.pfc.orientamais.domain.model.auth;

import java.util.UUID;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

public interface Profile {
  UUID getId();

  String getName();

  void fillFromRequest(AuthUser user, UserRegisterModelRequest request);
}
