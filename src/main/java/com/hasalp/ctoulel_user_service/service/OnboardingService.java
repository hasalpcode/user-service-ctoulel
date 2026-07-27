package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.client.TenantServiceClient;
import com.hasalp.ctoulel_user_service.dao.UserDao;
import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.SignupRequestDTO;
import com.hasalp.ctoulel_user_service.exception.ResourceExistsException;
import com.hasalp.ctoulel_user_service.exception.ResourceNotFoundException;
import com.hasalp.ctoulel_user_service.mapper.UserMapper;
import com.hasalp.ctoulel_user_service.model.Membership;
import com.hasalp.ctoulel_user_service.model.MembershipStatus;
import com.hasalp.ctoulel_user_service.model.Role;
import com.hasalp.ctoulel_user_service.model.User;
import com.hasalp.ctoulel_user_service.repository.MembershipRepository;
import com.hasalp.ctoulel_user_service.repository.RoleRepository;
import com.hasalp.ctoulel_user_service.security.JwtService;
import com.hasalp.ctoulel_user_service.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Orchestre le signup self-service : cree le tenant (tenant-service), le
 * User global, son Membership proprietaire, transfere la propriete du
 * tenant, puis cree l'abonnement choisi.
 *
 * Pas de saga/compensation : chaque etape ecrit dans un service/une base
 * differente, sans transaction distribuee. Si un appel echoue apres la
 * creation du tenant ou du user, rien n'annule ce qui precede - limite
 * assumee pour cette phase, a revisiter si ça devient un probleme reel.
 */
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final UserDao userDao;
    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantServiceClient tenantServiceClient;
    private final UserMapper mapper;

    public AuthResponseDTO signup(SignupRequestDTO request) {
        if (userDao.existsByEmail(request.email())) {
            throw new ResourceExistsException("Email déjà utilisé");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN introuvable"));

        UUID tenantId = tenantServiceClient.createTenant(request.tenantName(), request.subdomain());

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(adminRole);
        user = userDao.save(user);

        createOwnerMembership(tenantId, user.getUserId(), adminRole);

        tenantServiceClient.updateOwner(tenantId, user.getUserId());
        tenantServiceClient.createSubscription(tenantId, request.planId());

        String token = jwtService.generateToken(user, tenantId, adminRole.getName());

        return AuthResponseDTO.builder()
                .token(token)
                .user(mapper.toDTO(user))
                .build();
    }

    /**
     * Membership herite de TenantAwareEntity (@TenantId) : Hibernate peuple
     * tenant_id lui-meme a partir du TenantContext courant au moment de la
     * creation de la Session, quoi qu'on mette sur l'entite - il faut donc
     * poser le contexte AVANT cet appel. Ca ne fonctionne correctement que
     * parce que spring.jpa.open-in-view est desactive pour ce service : sans
     * ca, une Session unique resterait ouverte (et son tenant deja fige)
     * pour toute la duree de la requete HTTP, avant meme que ce code ne
     * s'execute (voir application.yml).
     */
    private void createOwnerMembership(UUID tenantId, Long userId, Role role) {
        TenantContext.set(tenantId);
        try {
            Membership membership = new Membership();
            membership.setUserId(userId);
            membership.setRole(role);
            membership.setStatus(MembershipStatus.ACTIVE);
            membershipRepository.save(membership);
        } finally {
            TenantContext.clear();
        }
    }
}
