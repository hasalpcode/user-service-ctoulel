package com.hasalp.ctoulel_user_service.repository;

import com.hasalp.ctoulel_user_service.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByTenantIdAndUserId(UUID tenantId, Long userId);
    List<Membership> findByUserId(Long userId);
    List<Membership> findByTenantId(UUID tenantId);
    boolean existsByTenantIdAndUserId(UUID tenantId, Long userId);

    /**
     * Requete native : contourne volontairement le filtre @TenantId
     * (WHERE tenant_id = ? implicite sur toute requete ORM), puisqu'on
     * cherche justement, avant tout login, dans QUELS tenants cet
     * utilisateur a acces - le contexte tenant n'existe pas encore a cet
     * instant (voir AuthController#resolveTenant).
     */
    @Query(value = "SELECT * FROM memberships WHERE user_id = :userId AND status = 'ACTIVE'", nativeQuery = true)
    List<Membership> findActiveByUserIdAcrossTenants(@Param("userId") Long userId);
}
