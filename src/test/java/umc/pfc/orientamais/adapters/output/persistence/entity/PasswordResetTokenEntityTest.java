package umc.pfc.orientamais.adapters.output.persistence.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenEntityTest {

    @InjectMocks
    private PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder().build();

    @Test
    void shouldBuildEntityWithAllFields() {
        Instant now = Instant.now();

        PasswordResetTokenEntity tokenEntity = PasswordResetTokenEntity.builder()
                .token("token")
                .email("user@email.com")
                .expiresAt(now.plusSeconds(3600))
                .createdAt(now)
                .build();

        assertEquals("token", tokenEntity.getToken());
        assertEquals("user@email.com", tokenEntity.getEmail());
        assertNotNull(tokenEntity.getExpiresAt());
        assertEquals(now, tokenEntity.getCreatedAt());
    }
}

