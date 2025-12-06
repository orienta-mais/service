package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import umc.pfc.orientamais.adapters.output.persistence.repository.RegistrationTokenRepository;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;
import umc.pfc.orientamais.domain.model.auth.RegistrationToken;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RegistrationTokenFactoryTest {

    @Mock
    private RegistrationTokenRepository tokenRepository;

    @InjectMocks
    private RegistrationTokenFactory factory;

    @BeforeEach
    void setUp() {
        // No-op, handled via annotations
    }

    @Test
    void createShouldReturnTokenWithRandomUuidAndExpiration() {
        String email = "teste@exemplo.com";
        RegistrationToken token = factory.create(email, AuthUserRole.MENTOR);

        assertEquals(email, token.getEmail());
        assertEquals(AuthUserRole.MENTOR, token.getRole());
        assertNotNull(token.getToken());
        assertDoesNotThrow(() -> UUID.fromString(token.getToken()));

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(now, token.getExpiration());
        assertTrue(diff.toHours() >= 23 && diff.toHours() <= 25);
        verifyNoInteractions(tokenRepository);
    }
}

