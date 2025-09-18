package umc.pfc.orientamais.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import umc.pfc.orientamais.adapters.input.rest.dto.response.LoginResponse;
import umc.pfc.orientamais.adapters.output.persistence.repository.AuthUserRepository;
import umc.pfc.orientamais.application.port.input.LoginUseCase;
import umc.pfc.orientamais.config.security.jwt.JwtProvider;
import umc.pfc.orientamais.domain.exceptions.NotFoundException;
import umc.pfc.orientamais.domain.model.AuthUser;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final AuthUserRepository authUserRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(String email, String password) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new NotFoundException("Senha inválida");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new LoginResponse("SUCCESS", "Login realizado com sucesso", accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        AuthUser user = jwtProvider.validateAndGetUser(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        return new LoginResponse("SUCCESS", "Token renovado com sucesso", newAccessToken, newRefreshToken);
    }
}
