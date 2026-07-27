package com.hasalp.ctoulel_user_service.repository;

import com.hasalp.ctoulel_user_service.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByTenantIdAndUserId(UUID tenantId, Long userId);
    List<Membership> findByUserId(Long userId);
    List<Membership> findByTenantId(UUID tenantId);
    boolean existsByTenantIdAndUserId(UUID tenantId, Long userId);
}
