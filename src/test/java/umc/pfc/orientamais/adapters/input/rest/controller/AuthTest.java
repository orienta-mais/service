package umc.pfc.orientamais.adapters.input.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.LoginRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.application.port.input.LoginUseCase;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTest {

    @InjectMocks
    private Auth auth;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private HttpServletRequest httpServletRequest;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("user@email.com", "123456");
        loginResponse = new LoginResponse(
                "SUCCESS",
                "Login realizado com sucesso",
                "accessToken",
                "refreshToken"
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        when(loginUseCase.login("user@email.com", "123456")).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = auth.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().status());
        assertEquals("Login realizado com sucesso", response.getBody().message());
        assertEquals("accessToken", response.getBody().accessToken());
        assertEquals("refreshToken", response.getBody().refreshToken());
        verify(loginUseCase).login("user@email.com", "123456");
    }

    @Test
    void shouldThrowWhenLoginFails() {
        when(loginUseCase.login("user@email.com", "123456"))
                .thenThrow(new NotFoundException("Invalid credentials"));

        assertThrows(NotFoundException.class, () -> auth.login(loginRequest));
        verify(loginUseCase).login("user@email.com", "123456");
    }

    @Test
    void shouldThrowNullPointerWhenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> auth.login(null));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
        when(loginUseCase.refreshToken("validRefreshToken")).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = auth.refreshToken(httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().status());
        assertEquals("Login realizado com sucesso", response.getBody().message());
        assertEquals("accessToken", response.getBody().accessToken());
        assertEquals("refreshToken", response.getBody().refreshToken());
        verify(loginUseCase).refreshToken("validRefreshToken");
    }

    @Test
    void shouldThrowWhenAuthorizationHeaderIsMissing() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
        verify(loginUseCase, never()).refreshToken(anyString());
    }

    @Test
    void shouldThrowWhenAuthorizationHeaderHasInvalidPrefix() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Token abc123");

        assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
        verify(loginUseCase, never()).refreshToken(anyString());
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(loginUseCase.refreshToken("expiredToken")).thenThrow(new InvalidOrExpiredTokenException());

        assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
        verify(loginUseCase).refreshToken("expiredToken");
    }

    @Test
    void shouldThrowWhenBearerHasNoToken() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");

        assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
        verify(loginUseCase, never()).refreshToken(anyString());
    }

    @Test
    void shouldHandleBearerWithExtraSpaces() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("  Bearer   token123  ");

        assertThrows(InvalidOrExpiredTokenException.class, () -> auth.refreshToken(httpServletRequest));
        verify(loginUseCase, never()).refreshToken(anyString());
    }
}
