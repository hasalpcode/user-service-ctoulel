package com.hasalp.ctoulel_user_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolveTenantRequestDTO(
        @NotBlank
        String email
) {
}
