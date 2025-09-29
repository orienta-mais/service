package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.application.port.input.LoginUseCase;
import umc.pfc.orientamais.config.security.jwt.JwtProvider;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.RefreshToken;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUserRepository authUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(String email, String password) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(password, authUser.getPassword())) {
            throw new NotFoundException("Email ou senha inválidos");
        }
        String accessToken = jwtProvider.generateAccessToken(authUser);
        String refreshToken = jwtProvider.generateRefreshToken(authUser);
        return new LoginResponse("SUCCESS", "Login realizado com sucesso", accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);
        if (refreshToken == null || jwtProvider.isRefreshTokenExpired(refreshToken)) {
            throw new NotFoundException("Refresh token expirado. Faça login novamente.");
        }
        String email = jwtProvider.validateAndGetUser(token);
        AuthUser user = authUserRepository.findByEmail(email).orElse(null);
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        return new LoginResponse("SUCCESS", "Token renovado com sucesso", newAccessToken, newRefreshToken);
    }
}
