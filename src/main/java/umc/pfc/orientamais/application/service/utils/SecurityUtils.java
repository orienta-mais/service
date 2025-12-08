package umc.pfc.orientamais.application.service.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import umc.pfc.orientamais.domain.model.auth.AuthUser;
import umc.pfc.orientamais.domain.model.auth.AuthUserRole;

import java.util.UUID;

public class SecurityUtils {
  public static UUID getCurrentProfileId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      throw new IllegalStateException("Usuário não autenticado");
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof String) {
      throw new IllegalStateException("Principal é uma String ao invés de AuthUser. Token inválido ou configuração incorreta.");
    }

    if (principal instanceof AuthUser authUser) {
      return authUser.getId();
    }

    throw new IllegalStateException("Tipo de principal não suportado: " + principal.getClass().getName());
  }

  public static AuthUserRole getCurrentUserRole() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      throw new IllegalStateException("Usuário não autenticado");
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof String) {
      throw new IllegalStateException("Principal é uma String ao invés de AuthUser. Token inválido ou configuração incorreta.");
    }

    if (principal instanceof AuthUser authUser) {
      return authUser.getRole();
    }

    throw new IllegalStateException("Tipo de principal não suportado: " + principal.getClass().getName());
  }
}
