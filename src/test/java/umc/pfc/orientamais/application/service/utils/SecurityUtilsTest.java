package umc.pfc.orientamais.application.service.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser();
        authUser.setId(UUID.randomUUID());
        authUser.setRole(AuthUserRole.MENTOR);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getCurrentProfileIdShouldReturnUserId() {
        authenticate();

        UUID id = SecurityUtils.getCurrentProfileId();

        assertEquals(authUser.getId(), id);
    }

    @Test
    void getCurrentProfileIdShouldThrowWhenNoAuth() {
        assertThrows(IllegalStateException.class, SecurityUtils::getCurrentProfileId);
    }

    @Test
    void getCurrentUserRoleShouldReturnRole() {
        authenticate();

        assertEquals(AuthUserRole.MENTOR, SecurityUtils.getCurrentUserRole());
    }

    @Test
    void getCurrentUserRoleShouldThrowWhenNoAuth() {
        assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUserRole);
    }
}

