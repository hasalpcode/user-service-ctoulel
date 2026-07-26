package com.hasalp.ctoulel_user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Porte la relation utilisateur <-> tenant : un User (identite globale)
 * peut avoir un Membership par tenant, avec un role propre a ce tenant.
 * L'admin unique d'un tenant n'est pas modelise ici - c'est
 * Tenant.ownerUserId, cote tenant-service, qui le garantit par construction.
 */
@Entity
@Table(name = "memberships", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "user_id"}))
@Getter
@Setter
public class Membership extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;
}
