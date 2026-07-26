package com.hasalp.ctoulel_user_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MembershipRequest(
        @NotNull
        UUID tenantId,

        @NotNull
        Long userId,

        @NotNull
        Long roleId
) {
}
