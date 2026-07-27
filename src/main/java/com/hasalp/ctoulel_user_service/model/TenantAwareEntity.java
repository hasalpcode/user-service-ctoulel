package com.hasalp.ctoulel_user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

/**
 * Racine commune de toute entite rattachee a un tenant. tenant_id est
 * NOT NULL des la creation : aucune ligne ne doit exister sans tenant.
 *
 * @TenantId fait ajouter automatiquement "WHERE tenant_id = ?" par Hibernate
 * sur chaque requete (voir security.TenantIdentifierResolver), a partir du
 * tenant courant pose par security.TenantContextFilter sur chaque requete
 * HTTP entrante.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity {

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}
