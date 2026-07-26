package com.hasalp.ctoulel_user_service.dto;

import com.hasalp.ctoulel_user_service.model.Membership;
import com.hasalp.ctoulel_user_service.model.MembershipStatus;

import java.util.UUID;

public record MembershipResponse(
        Long id,
        UUID tenantId,
        Long userId,
        Long roleId,
        String roleName,
        MembershipStatus status
) {
    public static MembershipResponse from(Membership membership) {
        return new MembershipResponse(
                membership.getId(),
                membership.getTenantId(),
                membership.getUserId(),
                membership.getRole().getRoleid(),
                membership.getRole().getName(),
                membership.getStatus()
        );
    }
}
