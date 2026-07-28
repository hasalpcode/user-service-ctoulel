package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dto.MembershipRequest;
import com.hasalp.ctoulel_user_service.dto.MembershipResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipService {
    MembershipResponse create(MembershipRequest request);
    List<MembershipResponse> findByUserId(Long userId);
    List<MembershipResponse> findByTenantId(UUID tenantId);
    Optional<MembershipResponse> findByTenantIdAndUserId(UUID tenantId, Long userId);
    MembershipResponse updateRole(UUID tenantId, Long userId, Long roleId);
}
