package com.hasalp.ctoulel_user_service.controller;

import com.hasalp.ctoulel_user_service.dto.MembershipRequest;
import com.hasalp.ctoulel_user_service.dto.MembershipResponse;
import com.hasalp.ctoulel_user_service.exception.ResourceNotFoundException;
import com.hasalp.ctoulel_user_service.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipResponse create(@Valid @RequestBody MembershipRequest request) {
        return membershipService.create(request);
    }

    @GetMapping("/user/{userId}")
    public List<MembershipResponse> findByUser(@PathVariable Long userId) {
        return membershipService.findByUserId(userId);
    }

    @GetMapping("/tenant/{tenantId}")
    public List<MembershipResponse> findByTenant(@PathVariable UUID tenantId) {
        return membershipService.findByTenantId(tenantId);
    }

    /**
     * Utilise par le gateway/user-service au login pour verifier si
     * l'utilisateur a un acces actif au tenant resolu par sous-domaine.
     */
    @GetMapping("/tenant/{tenantId}/user/{userId}")
    public MembershipResponse findByTenantAndUser(@PathVariable UUID tenantId, @PathVariable Long userId) {
        return membershipService.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun acces pour l'utilisateur " + userId + " sur le tenant " + tenantId));
    }

    /**
     * Change le role d'un membre au sein d'un tenant (ex: promotion
     * USER -> COMPTABLE) - distinct de PATCH /users/{id}/role/{roleId} qui
     * ne touche que le role global historique, sans effet sur les droits
     * reels par tenant.
     */
    @PatchMapping("/tenant/{tenantId}/user/{userId}/role/{roleId}")
    public MembershipResponse updateRole(
            @PathVariable UUID tenantId, @PathVariable Long userId, @PathVariable Long roleId) {
        return membershipService.updateRole(tenantId, userId, roleId);
    }
}
