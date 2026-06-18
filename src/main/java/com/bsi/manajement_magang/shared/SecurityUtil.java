package com.bsi.manajement_magang.shared;

import org.springframework.security.core.Authentication;
import java.util.UUID;

public final class SecurityUtil {
    private SecurityUtil() {}

    /**
     * Extracts the authenticated user's UUID from the SecurityContext.
     * Throws UNAUTHORIZED if the principal is absent or not a UUID
     * (e.g. anonymous "anonymousUser" string from Spring's default filter).
     */
    public static UUID requireUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UUID id) {
            return id;
        }
        throw DomainException.unauthorized("Not authenticated");
    }
}
