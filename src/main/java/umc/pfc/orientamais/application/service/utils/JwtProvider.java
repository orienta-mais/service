package umc.pfc.orientamais.application.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentorRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.MentoredRepository;
import umc.pfc.orientamais.adapters.output.persistence.repository.RefreshTokenRepository;
import umc.pfc.orientamais.domain.exceptions.InternalErrorException;
import umc.pfc.orientamais.domain.exceptions.InvalidOrExpiredTokenException;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.Profile;
import umc.pfc.orientamais.domain.model.auth.RefreshToken;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String issuer = "orienta-mais";
    private final RefreshTokenRepository refreshTokenRepository;
    private final MentorRepository mentorRepository;
    private final MentoredRepository mentoredRepository;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration-ms}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenValidity;

    public String generateAccessToken(AuthUser userAuth) {
        try {
            Profile profile = findProfile(userAuth);
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(userAuth.getEmail())
                    .withClaim("id", profile.getId().toString())
                    .withClaim("name", profile.getName())
                    .withClaim("role", userAuth.getRole().name())
                    .withExpiresAt(generateExpirationDate(accessTokenValidity))
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new InternalErrorException("Erro ao gerar token JWT");
        }
    }

    public String generateRefreshToken(AuthUser user) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer(issuer)
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
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (TokenExpiredException e) {
            throw new InvalidOrExpiredTokenException();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant generateExpirationDate(Long expirationTimeInMs) {
        return Instant.now().plusMillis(expirationTimeInMs);
    }

    private Profile findProfile(AuthUser userAuth) {
        return switch (userAuth.getRole()) {
            case MENTOR -> mentorRepository.findByUserId(userAuth.getId())
                    .orElseThrow(() -> new InternalErrorException("Perfil de mentor não encontrado"));
            case MENTORED -> mentoredRepository.findByUserId(userAuth.getId())
                    .orElseThrow(() -> new InternalErrorException("Perfil de mentorado não encontrado"));
            case ADMIN -> userAuth;
        };
    }
}
