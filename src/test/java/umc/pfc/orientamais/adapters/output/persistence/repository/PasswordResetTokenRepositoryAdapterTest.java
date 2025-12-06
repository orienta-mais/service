package umc.pfc.orientamais.adapters.output.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.pfc.orientamais.adapters.output.persistence.entity.PasswordResetTokenEntity;
import umc.pfc.orientamais.domain.model.auth.PasswordResetToken;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenRepositoryAdapterTest {

    @InjectMocks
    private PasswordResetTokenRepositoryAdapter adapter;

    @Mock
    private SpringDataPasswordResetTokenRepository jpa;

    private PasswordResetToken token;
    private PasswordResetTokenEntity entity;

    @BeforeEach
    void setUp() {
        token = PasswordResetToken.builder()
                .token("token")
                .email("user@email.com")
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();

        entity = PasswordResetTokenEntity.builder()
                .token(token.token())
                .email(token.email())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .build();
    }

    @Test
    void shouldSaveTokenSuccessfully() {
        when(jpa.save(any(PasswordResetTokenEntity.class))).thenReturn(entity);

        PasswordResetToken saved = adapter.save(token);

        assertNotNull(saved);
        assertEquals(token.token(), saved.token());
        verify(jpa).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    void shouldFindByTokenSuccessfully() {
        when(jpa.findById("token")).thenReturn(Optional.of(entity));

        Optional<PasswordResetToken> result = adapter.findByToken("token");

        assertTrue(result.isPresent());
        assertEquals(token.email(), result.get().email());
        verify(jpa).findById("token");
    }

    @Test
    void shouldReturnEmptyWhenTokenNotFound() {
        when(jpa.findById("token")).thenReturn(Optional.empty());

        Optional<PasswordResetToken> result = adapter.findByToken("token");

        assertFalse(result.isPresent());
        verify(jpa).findById("token");
    }

    @Test
    void shouldFindByEmailSuccessfully() {
        when(jpa.findByEmail("user@email.com")).thenReturn(Optional.of(entity));

        Optional<PasswordResetToken> result = adapter.findByEmail("user@email.com");

        assertTrue(result.isPresent());
        assertEquals(token.token(), result.get().token());
        verify(jpa).findByEmail("user@email.com");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        when(jpa.findByEmail("user@email.com")).thenReturn(Optional.empty());

        Optional<PasswordResetToken> result = adapter.findByEmail("user@email.com");

        assertTrue(result.isEmpty());
        verify(jpa).findByEmail("user@email.com");
    }

    @Test
    void shouldDeleteTokenSuccessfully() {
        adapter.delete(token);

        verify(jpa).deleteById(token.token());
    }

    @Test
    void shouldDeleteByEmailSuccessfully() {
        adapter.deleteByEmail("user@email.com");

        verify(jpa).deleteByEmail("user@email.com");
    }
}

