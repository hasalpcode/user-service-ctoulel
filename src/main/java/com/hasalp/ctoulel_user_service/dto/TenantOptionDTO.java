package com.hasalp.ctoulel_user_service.dto;

import java.util.UUID;

public record TenantOptionDTO(
        UUID tenantId,
        String subdomain,
        String tenantName
) {
}
