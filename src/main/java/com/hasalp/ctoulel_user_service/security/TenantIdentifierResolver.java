package com.hasalp.ctoulel_user_service.security;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resout le tenant courant pour le filtre Hibernate @TenantId, a partir du
 * TenantContext peuple par TenantContextFilter sur chaque requete. En dehors
 * d'une requete porteuse d'un tenant (taches internes, demarrage...), aucun
 * UUID ne matchera jamais aucune ligne : on ferme par defaut plutot que
 * d'ouvrir sur toutes les donnees.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID> {

    private static final UUID NO_TENANT = new UUID(0L, 0L);

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        UUID tenantId = TenantContext.get();
        return tenantId != null ? tenantId : NO_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
