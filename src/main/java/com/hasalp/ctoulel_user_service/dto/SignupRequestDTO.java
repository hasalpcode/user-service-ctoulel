package com.hasalp.ctoulel_user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record SignupRequestDTO(

        @NotBlank
        String tenantName,

        @NotBlank
        @Pattern(regexp = "^[a-z0-9-]+$", message = "le sous-domaine ne peut contenir que des minuscules, chiffres et tirets")
        String subdomain,

        @NotNull
        UUID planId,

        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
