package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTokenFactoryTest {

    private RegistrationTokenFactory factory;

    @BeforeEach
    void setUp() {
        RegistrationTokenRepository tokenRepository = Mockito.mock(RegistrationTokenRepository.class);
        factory = new RegistrationTokenFactory(tokenRepository);
    }

    @Test
    void shouldCreateRegistrationTokenCorrectly() {
        String email = "teste@exemplo.com";
        AuthUserRole role = AuthUserRole.MENTOR;

        RegistrationToken token = factory.create(email, role);

        assertNotNull(token);
        assertEquals(email, token.getEmail());
        assertEquals(role, token.getRole());
        assertNotNull(token.getToken());

        // Validar expiração com margem de tolerância de 1 minuto
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, token.getExpiration());
        long diffMinutes = duration.toMinutes();
        assertTrue(diffMinutes >= 23 * 60 && diffMinutes <= 24 * 60, "Token deve expirar entre 23h e 24h");
    }
}
