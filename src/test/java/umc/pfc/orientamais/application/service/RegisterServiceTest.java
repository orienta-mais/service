package umc.pfc.orientamais.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.AuthUserRole;
import umc.pfc.orientamais.domain.model.RegistrationToken;
import umc.pfc.orientamais.domain.model.mentor.Mentor;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    private AuthUserRepository authUserRepository;
    private RegistrationTokenRepository tokenRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private MentorRepository mentorRepository;
    private MentoredRepository mentoredRepository;

    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        authUserRepository = mock(AuthUserRepository.class);
        tokenRepository = mock(RegistrationTokenRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        mentorRepository = mock(MentorRepository.class);
        mentoredRepository = mock(MentoredRepository.class);

        registerService = new RegisterService(authUserRepository, tokenRepository, passwordEncoder,
                mentorRepository, mentoredRepository);
    }

    @Test
    void shouldRegisterUserSuccessfullyAsMentor() {
        RegistrationToken token = new RegistrationToken(UUID.randomUUID(), "email@exemplo.com",
                "token123", LocalDateTime.now().plusHours(1), AuthUserRole.MENTOR);

        UserRegisterModelRequest request = new UserRegisterModelRequest(
                "email@exemplo.com",
                "Name",
                "Last Name",
                "senha123",
                LocalDate.of(2002, 6, 21),
                null,
                null,
                "SP",
                "BR",
                AuthUserRole.MENTOR,
                "token123"
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPass");

        registerService.register(request, AuthUserRole.MENTOR);

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserRepository).save(userCaptor.capture());
        AuthUser savedUser = userCaptor.getValue();
        assertEquals("email@exemplo.com", savedUser.getEmail());
        assertEquals("encodedPass", savedUser.getPassword());

        verify(mentorRepository).save(any(Mentor.class));
        verify(tokenRepository).delete(token);
    }

    @Test
    void shouldRegisterUserSuccessfullyAsMentored() {
        RegistrationToken token = new RegistrationToken(UUID.randomUUID(), "email@exemplo.com",
                "token123", LocalDateTime.now().plusHours(1), AuthUserRole.MENTORED);

        UserRegisterModelRequest request = new UserRegisterModelRequest(
                "email@exemplo.com",
                "Name",
                "Last Name",
                "senha123",
                LocalDate.of(2002, 6, 21),
                null,
                null,
                "SP",
                "BR",
                AuthUserRole.MENTORED,
                "token123"
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPass");

        registerService.register(request, AuthUserRole.MENTORED);

        verify(authUserRepository).save(any(AuthUser.class));
        verify(mentoredRepository).save(any(Mentored.class));
        verify(tokenRepository).delete(token);
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        when(tokenRepository.findByToken("tokenInvalido")).thenReturn(Optional.empty());

        UserRegisterModelRequest request = new UserRegisterModelRequest(
                "email@exemplo.com",
                "Name",
                "Last Name",
                "senha123",
                LocalDate.of(2002, 6, 21),
                null,
                null,
                "SP",
                "BR",
                AuthUserRole.MENTOR,
                "tokenInvalido"
        );

        assertThrows(InvalidOrExpiredTokenException.class,
                () -> registerService.register(request, AuthUserRole.MENTOR));
    }

    @Test
    void shouldThrowWhenTokenExpired() {
        RegistrationToken token = new RegistrationToken(UUID.randomUUID(), "email@exemplo.com",
                "token123", LocalDateTime.now().minusHours(1), AuthUserRole.MENTOR);

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        UserRegisterModelRequest request = new UserRegisterModelRequest(
                "email@exemplo.com",
                "Name",
                "Last Name",
                "senha123",
                LocalDate.of(2002, 6, 21),
                null,
                null,
                "SP",
                "BR",
                AuthUserRole.MENTOR,
                "token123"
        );

        assertThrows(InvalidOrExpiredTokenException.class,
                () -> registerService.register(request, AuthUserRole.MENTOR));
    }
}
