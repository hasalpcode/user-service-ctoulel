package com.hasalp.ctoulel_user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Racine commune de toute entite rattachee a un tenant. tenant_id est
 * NOT NULL des la creation : aucune ligne ne doit exister sans tenant.
 *
 * Le filtre Hibernate automatique (@TenantId + CurrentTenantIdentifierResolver)
 * n'est pas encore active ici - il arrive en Phase 3, une fois le
 * TenantContext propage par le gateway (Phase 2). Pour l'instant, tenant_id
 * est une colonne normale que chaque service doit renseigner explicitement.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}
