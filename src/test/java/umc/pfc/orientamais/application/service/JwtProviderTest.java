package umc.pfc.orientamais.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.application.service.utils.JwtProvider;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

class JwtProviderTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentoredRepository mentoredRepository;

  @InjectMocks private JwtProvider jwtProvider;

  private AuthUser mentorUser;
  private AuthUser mentoredUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(jwtProvider, "secretKey", "test-secret");
    ReflectionTestUtils.setField(jwtProvider, "accessTokenValidity", 3600000L); // 1h
    ReflectionTestUtils.setField(jwtProvider, "refreshTokenValidity", 86400000L); // 24h

    mentorUser =
        new AuthUser(UUID.randomUUID(), "mentor@test.com", "password", AuthUserRole.MENTOR, true);
    mentoredUser =
        new AuthUser(UUID.randomUUID(), "mentored@test.com", "password", AuthUserRole.MENTORED, true);
  }

  @Test
  void shouldGenerateAccessTokenForMentor() {
    Mentor profile = new Mentor();
    profile.setId(UUID.randomUUID());
    profile.setName("Mentor Name");

    when(mentorRepository.findByUserId(mentorUser.getId())).thenReturn(Optional.of(profile));

    String token = jwtProvider.generateAccessToken(mentorUser);
    assertNotNull(token);
  }

  @Test
  void shouldGenerateAccessTokenForMentored() {
    Mentored profile = new Mentored();
    profile.setId(UUID.randomUUID());
    profile.setName("Mentored Name");

    when(mentoredRepository.findByUserId(mentoredUser.getId())).thenReturn(Optional.of(profile));

    String token = jwtProvider.generateAccessToken(mentoredUser);
    assertNotNull(token);
  }

  @Test
  void shouldThrowInternalErrorExceptionIfProfileNotFound() {
    when(mentorRepository.findByUserId(mentorUser.getId())).thenReturn(Optional.empty());
    assertThrows(InternalErrorException.class, () -> jwtProvider.generateAccessToken(mentorUser));
  }

  @Test
  void shouldValidateAndGetUser() {
    String validToken = jwtProvider.generateRefreshToken(mentorUser);
    String email = jwtProvider.validateAndGetUser(validToken);
    assertEquals(mentorUser.getEmail(), email);
  }

  @Test
  void shouldReturnNullForInvalidToken() {
    String invalidToken = "invalid.token";
    String email = jwtProvider.validateAndGetUser(invalidToken);
    assertNull(email);
  }
}
