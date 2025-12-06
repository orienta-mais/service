package umc.pfc.orientamais.application.port.input;

import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;

public interface LoginUseCase {
  LoginResponse login(String email, String password);

  LoginResponse refreshToken(String refreshToken);
}
