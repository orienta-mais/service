package umc.pfc.orientamais.adapters.input.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import umc.pfc.orientamais.adapters.input.rest.dto.request.LoginRequest;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.application.port.input.LoginUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthTest {

    @Mock
    private LoginUseCase loginUseCase;

    @InjectMocks
    private Auth auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnLoginResponse() {
        LoginRequest request = new LoginRequest("user@email.com", "123456");
        LoginResponse expectedResponse = new LoginResponse(
                "SUCCESS",
                "Login realizado com sucesso",
                "accessToken",
                "refreshToken"
        );

        when(loginUseCase.login(request.email(), request.password())).thenReturn(expectedResponse);

        ResponseEntity<LoginResponse> response = auth.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(loginUseCase, times(1)).login(request.email(), request.password());
    }

    @Test
    void refreshToken_shouldReturnLoginResponse() {
        String refreshToken = "refresh123";
        LoginResponse expectedResponse = new LoginResponse(
                "SUCCESS",
                "Login realizado com sucesso",
                "accessToken",
                "refreshToken"
        );

        when(loginUseCase.refreshToken(refreshToken)).thenReturn(expectedResponse);

        ResponseEntity<LoginResponse> response = auth.refreshToken(refreshToken);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(loginUseCase, times(1)).refreshToken(refreshToken);
    }
}
