package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.client.TenantServiceClient;
import com.hasalp.ctoulel_user_service.dao.UserDao;
import com.hasalp.ctoulel_user_service.dto.TenantOptionDTO;
import com.hasalp.ctoulel_user_service.exception.ResourceNotFoundException;
import com.hasalp.ctoulel_user_service.model.User;
import com.hasalp.ctoulel_user_service.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resout les tenants (caisses) accessibles a un utilisateur avant meme
 * qu'il ne soit connecte, a partir de son email/telephone seul - utilise
 * par l'ecran de login pour retrouver automatiquement le sous-domaine,
 * plutot que de le demander a l'utilisateur.
 */
@Service
@RequiredArgsConstructor
public class TenantLookupService {

    private final UserDao userDao;
    private final MembershipRepository membershipRepository;
    private final TenantServiceClient tenantServiceClient;

    public List<TenantOptionDTO> resolveTenants(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return membershipRepository.findActiveByUserIdAcrossTenants(user.getUserId()).stream()
                .map(membership -> {
                    TenantServiceClient.TenantInfo info = tenantServiceClient.getTenant(membership.getTenantId());
                    return new TenantOptionDTO(info.tenantId(), info.subdomain(), info.name());
                })
                .toList();
    }
}
