package umc.pfc.orientamais.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.application.service.utils.JwtProvider;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.RefreshToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private JwtProvider jwtProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthUserRepository authUserRepository;
    private BCryptPasswordEncoder passwordEncoder;

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        authUserRepository = mock(AuthUserRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        loginService = new LoginService(jwtProvider, refreshTokenRepository, authUserRepository, passwordEncoder);
    }

    @Test
    void shouldLoginSuccessfully() {
        AuthUser user = new AuthUser();
        user.setEmail("teste@exemplo.com");
        user.setPassword("encoded");

        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha", "encoded")).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(user)).thenReturn("refresh-token");

        LoginResponse response = loginService.login(user.getEmail(), "senha");

        assertEquals("SUCCESS", response.status());
        assertEquals("Login realizado com sucesso", response.message());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        when(authUserRepository.findByEmail("email@invalido.com")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> loginService.login("email@invalido.com", "senha"));

        assertEquals("Email ou senha inválidos", ex.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIncorrect() {
        AuthUser user = new AuthUser();
        user.setEmail("teste@exemplo.com");
        user.setPassword("encoded");

        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "encoded")).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> loginService.login(user.getEmail(), "senhaErrada"));

        assertEquals("Email ou senha inválidos", ex.getMessage());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken("refresh-token");

        AuthUser user = new AuthUser();
        user.setEmail("teste@exemplo.com");

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(storedToken));
        when(jwtProvider.isRefreshTokenExpired(storedToken)).thenReturn(false);
        when(jwtProvider.validateAndGetUser("refresh-token")).thenReturn(user.getEmail());
        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(user)).thenReturn("new-access");
        when(jwtProvider.generateRefreshToken(user)).thenReturn("new-refresh");

        LoginResponse response = loginService.refreshToken("Bearer refresh-token");

        assertEquals("SUCCESS", response.status());
        assertEquals("Token renovado com sucesso", response.message());
        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());

        verify(refreshTokenRepository).delete(storedToken);
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> loginService.refreshToken("Bearer invalid"));

        assertEquals("Refresh token não encontrado ou inválido.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenRefreshTokenExpired() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken("refresh-token");

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(storedToken));
        when(jwtProvider.isRefreshTokenExpired(storedToken)).thenReturn(true);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> loginService.refreshToken("Bearer refresh-token"));

        assertEquals("Refresh token expirado. Faça login novamente.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUserNotFoundDuringRefresh() {
        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken("refresh-token");

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(storedToken));
        when(jwtProvider.isRefreshTokenExpired(storedToken)).thenReturn(false);
        when(jwtProvider.validateAndGetUser("refresh-token")).thenReturn("email@invalido.com");
        when(authUserRepository.findByEmail("email@invalido.com")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> loginService.refreshToken("Bearer refresh-token"));

        assertEquals("Usuário não encontrado.", ex.getMessage());
    }
}
