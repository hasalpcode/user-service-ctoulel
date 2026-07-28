package com.hasalp.ctoulel_user_service.controller;

import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetRequestDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetResponseDTO;
import com.hasalp.ctoulel_user_service.dto.ResolveTenantRequestDTO;
import com.hasalp.ctoulel_user_service.dto.SignupRequestDTO;
import com.hasalp.ctoulel_user_service.dto.TenantOptionDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;
import com.hasalp.ctoulel_user_service.service.OnboardingService;
import com.hasalp.ctoulel_user_service.service.TenantLookupService;
import com.hasalp.ctoulel_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;
    private final OnboardingService onboardingService;
    private final TenantLookupService tenantLookupService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request) {
        return ResponseEntity.ok(onboardingService.signup(request));
    }

    /**
     * Retrouve les caisses (tenants) auxquelles cet utilisateur a acces,
     * avant meme d'etre connecte - permet au client de resoudre le
     * sous-domaine automatiquement plutot que de le demander.
     */
    @PostMapping("/resolve-tenant")
    public ResponseEntity<List<TenantOptionDTO>> resolveTenant(
            @Valid @RequestBody ResolveTenantRequestDTO request) {
        return ResponseEntity.ok(tenantLookupService.resolveTenants(request.email()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody UserRequestDTO request,
            @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantId) {
        return ResponseEntity.ok(service.login(request, tenantId));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponseDTO> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO request) {
        return ResponseEntity.ok(service.requestPasswordReset(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(
            @Valid @RequestBody PasswordResetDTO request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }
}

