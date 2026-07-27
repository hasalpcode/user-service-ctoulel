package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dto.MembershipRequest;
import com.hasalp.ctoulel_user_service.dto.MembershipResponse;
import com.hasalp.ctoulel_user_service.exception.ResourceExistsException;
import com.hasalp.ctoulel_user_service.exception.ResourceNotFoundException;
import com.hasalp.ctoulel_user_service.model.Membership;
import com.hasalp.ctoulel_user_service.model.MembershipStatus;
import com.hasalp.ctoulel_user_service.model.Role;
import com.hasalp.ctoulel_user_service.repository.MembershipRepository;
import com.hasalp.ctoulel_user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public MembershipResponse create(MembershipRequest request) {
        if (membershipRepository.existsByTenantIdAndUserId(request.tenantId(), request.userId())) {
            throw new ResourceExistsException(
                    "Un membership existe deja pour cet utilisateur dans ce tenant");
        }
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role introuvable: " + request.roleId()));

        Membership membership = new Membership();
        membership.setTenantId(request.tenantId());
        membership.setUserId(request.userId());
        membership.setRole(role);
        membership.setStatus(MembershipStatus.ACTIVE);

        return MembershipResponse.from(membershipRepository.save(membership));
    }

    @Override
    public List<MembershipResponse> findByUserId(Long userId) {
        return membershipRepository.findByUserId(userId).stream()
                .map(MembershipResponse::from)
                .toList();
    }

    @Override
    public List<MembershipResponse> findByTenantId(UUID tenantId) {
        return membershipRepository.findByTenantId(tenantId).stream()
                .map(MembershipResponse::from)
                .toList();
    }

    @Override
    public Optional<MembershipResponse> findByTenantIdAndUserId(UUID tenantId, Long userId) {
        return membershipRepository.findByTenantIdAndUserId(tenantId, userId)
                .map(MembershipResponse::from);
    }
}
