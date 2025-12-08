package umc.pfc.orientamais.application.service.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RefreshToken;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private MentorRepository mentorRepository;

  @Mock private MentoredRepository mentoredRepository;

  @InjectMocks private JwtProvider jwtProvider;

  private AuthUser mentorUser;
  private Mentor mentorProfile;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtProvider, "secretKey", "secret");
    ReflectionTestUtils.setField(jwtProvider, "accessTokenValidity", 3600000L);
    ReflectionTestUtils.setField(jwtProvider, "refreshTokenValidity", 7200000L);

    mentorUser = new AuthUser();
    mentorUser.setId(UUID.randomUUID());
    mentorUser.setEmail("mentor@test.com");
    mentorUser.setRole(AuthUserRole.MENTOR);

    mentorProfile = new Mentor();
    mentorProfile.setId(UUID.randomUUID());
    mentorProfile.setName("Mentor Name");

    when(mentorRepository.findByUserId(mentorUser.getId())).thenReturn(Optional.of(mentorProfile));
  }

  @Test
  void generateAccessTokenShouldIncludeProfileClaims() {
    String token = jwtProvider.generateAccessToken(mentorUser);

    DecodedJWT decoded = JWT.decode(token);
    assertEquals("mentor@test.com", decoded.getSubject());
    assertEquals("Mentor Name", decoded.getClaim("name").asString());
  }

  @Test
  void generateAccessTokenShouldThrowWhenCreationFails() {
    ReflectionTestUtils.setField(jwtProvider, "secretKey", null);

    assertThrows(InternalErrorException.class, () -> jwtProvider.generateAccessToken(mentorUser));
  }

  @Test
  void generateRefreshTokenShouldPersistToken() {
    String token = jwtProvider.generateRefreshToken(mentorUser);

    assertNotNull(token);
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }

  @Test
  void isRefreshTokenExpiredShouldCompareDates() {
    RefreshToken token = new RefreshToken();
    token.setExpiryDate(Instant.now().minusSeconds(5));

    assertTrue(jwtProvider.isRefreshTokenExpired(token));
  }

  @Test
  void validateAndGetUserShouldReturnSubject() {
    String token = jwtProvider.generateAccessToken(mentorUser);

    String subject = jwtProvider.validateAndGetUser(token);

    assertEquals("mentor@test.com", subject);
  }

  @Test
  void validateAndGetUserShouldReturnNullForInvalidToken() {
    String invalidToken = JWT.create().withIssuer("other").sign(Algorithm.HMAC256("secret"));

    assertNull(jwtProvider.validateAndGetUser(invalidToken));
  }

  @Test
  void validateAndGetUserShouldThrowForExpiredToken() throws InterruptedException {
    ReflectionTestUtils.setField(jwtProvider, "accessTokenValidity", 1L);
    String token = jwtProvider.generateAccessToken(mentorUser);
    Thread.sleep(2L);

    assertThrows(InvalidOrExpiredTokenException.class, () -> jwtProvider.validateAndGetUser(token));
  }

  @Test
  void findProfileShouldReturnMentoredForRole() {
    AuthUser mentoredUser = new AuthUser();
    mentoredUser.setId(UUID.randomUUID());
    mentoredUser.setEmail("mentored@test.com");
    mentoredUser.setRole(AuthUserRole.MENTORED);

    Mentored mentored = new Mentored();
    mentored.setId(UUID.randomUUID());
    mentored.setLastName("Student");

    when(mentoredRepository.findByUserId(mentoredUser.getId())).thenReturn(Optional.of(mentored));

    String token = jwtProvider.generateAccessToken(mentoredUser);
    assertNotNull(token);
  }
}
