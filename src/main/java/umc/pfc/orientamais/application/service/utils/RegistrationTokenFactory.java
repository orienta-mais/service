package umc.pfc.orientamais.application.service.utils;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

@Component
@RequiredArgsConstructor
public class RegistrationTokenFactory {

  private final RegistrationTokenRepository tokenRepository;

  public RegistrationToken create(String email, AuthUserRole role) {
    return new RegistrationToken(
        email, UUID.randomUUID().toString(), LocalDateTime.now().plusHours(24), role);
  }
}
