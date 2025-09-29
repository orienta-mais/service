package umc.pfc.orientamais.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.RefreshToken;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenValidity;

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(AuthUser user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer("orienta-mais")
                    .withSubject(user.getEmail())
                    .withClaim("role", String.valueOf(user.getRole()))
                    .withExpiresAt(this.generateExpirationDate(accessTokenValidity))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new InternalErrorException("Erro ao gerar token JWT");
        }
    }

    public String generateRefreshToken(AuthUser user) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer("orienta-mais")
                .withSubject(user.getEmail())
                .withClaim("role", String.valueOf(user.getRole()))
                .withExpiresAt(this.generateExpirationDate(refreshTokenValidity))
                .sign(algorithm);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenValidity));
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public String validateAndGetUser(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer("orienta-mais")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant generateExpirationDate(Long expirationTimeInMs) {
        return Instant.now().plusMillis(expirationTimeInMs);
    }
}
