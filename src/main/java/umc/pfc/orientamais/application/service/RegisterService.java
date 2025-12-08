package umc.pfc.orientamais.application.service;

import jakarta.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.application.port.input.RegisterUseCase;
import umc.pfc.orientamais.application.service.utils.ProfileMapping;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.Profile;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Service
@Transactional
public class RegisterService implements RegisterUseCase {

  private final AuthUserRepository authUserRepository;
  private final RegistrationTokenRepository tokenRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final Map<AuthUserRole, ProfileMapping<? extends Profile>> profileMappings;

  public RegisterService(
      AuthUserRepository authUserRepository,
      RegistrationTokenRepository tokenRepository,
      BCryptPasswordEncoder passwordEncoder,
      MentorRepository mentorRepository,
      MentoredRepository mentoredRepository) {
    this.authUserRepository = authUserRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;

    this.profileMappings =
        Map.of(
            AuthUserRole.MENTOR, new ProfileMapping<>(mentorRepository, Mentor::new),
            AuthUserRole.MENTORED, new ProfileMapping<>(mentoredRepository, Mentored::new));
  }

  @Override
  public void register(UserRegisterModelRequest request, AuthUserRole role) {
    RegistrationToken token = validateToken(request.token());
    AuthUser user = createAuthUser(request, role);
    authUserRepository.save(user);

    createProfile(user, role, request);

    tokenRepository.delete(token);
  }

  private RegistrationToken validateToken(String tokenStr) {
    RegistrationToken token =
        tokenRepository.findByToken(tokenStr).orElseThrow(InvalidOrExpiredTokenException::new);

    if (token.getExpiration().isBefore(LocalDateTime.now())) {
      throw new InvalidOrExpiredTokenException();
    }

    return token;
  }

  private AuthUser createAuthUser(UserRegisterModelRequest request, AuthUserRole role) {
    String encryptedPassword = passwordEncoder.encode(request.password());
    String safeEmail = request.email().replace("%2B", "+");
    String decodedEmail = URLDecoder.decode(safeEmail, StandardCharsets.UTF_8);
    return new AuthUser(decodedEmail, encryptedPassword, role, false);
  }

  private <T extends Profile> void createProfile(
      AuthUser user, AuthUserRole role, UserRegisterModelRequest request) {
    @SuppressWarnings("unchecked")
    ProfileMapping<T> mapping = (ProfileMapping<T>) profileMappings.get(role);

    if (mapping == null) {
      throw new IllegalArgumentException("Role não suportado: " + role);
    }

    T profile = mapping.factory().get();
    profile.fillFromRequest(user, request);
    mapping.repository().save(profile);
  }
}
