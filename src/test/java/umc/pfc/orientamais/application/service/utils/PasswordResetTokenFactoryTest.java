package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenFactoryTest {

    @InjectMocks
    private PasswordResetTokenFactory factory;

    @BeforeEach
    void setUp() {
        // No dependencies to mock
    }

    @Test
    void createShouldPopulateFieldsWithTtl() {
        PasswordResetToken token = factory.create("email@test.com", 600);

        assertEquals("email@test.com", token.email());
        assertNull(token.token());
        assertNotNull(token.createdAt());
        assertNotNull(token.expiresAt());
        Duration ttl = Duration.between(token.createdAt(), token.expiresAt());
        assertEquals(600, ttl.getSeconds());
    }

    @Test
    void createShouldHandleZeroTtl() {
        PasswordResetToken token = factory.create("email@test.com", 0);

        assertEquals(token.createdAt(), token.expiresAt());
    }
}
