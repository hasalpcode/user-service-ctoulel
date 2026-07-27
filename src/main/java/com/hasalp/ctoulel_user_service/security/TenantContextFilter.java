package com.hasalp.ctoulel_user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Peuple le TenantContext a partir de l'en-tete X-Tenant-Id pose par le
 * gateway (deja verifie en amont : TenantResolutionFilter + JwtValidationFilter).
 * Nettoye systematiquement en fin de requete pour eviter une fuite de tenant
 * d'une requete a l'autre sur un thread reutilise par le pool.
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String TENANT_ID_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(TENANT_ID_HEADER);
        try {
            if (header != null && !header.isBlank()) {
                TenantContext.set(UUID.fromString(header));
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
