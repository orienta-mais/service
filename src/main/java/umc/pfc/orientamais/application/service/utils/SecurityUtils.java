package umc.pfc.orientamais.application.service.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import umc.pfc.orientamais.domain.model.AuthUser;
import umc.pfc.orientamais.domain.model.AuthUserRole;

import java.util.UUID;

public class SecurityUtils {
    public static UUID getCurrentProfileId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        return authUser.getId();
    }

    public static AuthUserRole getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        return authUser.getRole();
    }
}