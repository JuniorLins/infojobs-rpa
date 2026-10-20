package com.rpa.infojobs.security;

import com.rpa.infojobs.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User)) {
            throw new IllegalStateException("Nenhum usuário autenticado");
        }
        return (User) auth.getPrincipal();
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
