package com.hasalp.ctoulel_user_service.security;

import com.hasalp.ctoulel_user_service.model.Membership;
import com.hasalp.ctoulel_user_service.model.MembershipStatus;
import com.hasalp.ctoulel_user_service.model.Role;
import com.hasalp.ctoulel_user_service.repository.MembershipRepository;
import com.hasalp.ctoulel_user_service.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Meme principe que le TenantIsolationTest de ctoulel-member-service :
 * verifie que le filtre Hibernate @TenantId isole reellement les tenants,
 * cette fois sur Membership (seule entite tenant-aware de ce service).
 * TenantContext doit etre pose AVANT d'ouvrir la transaction, puisque
 * Hibernate resout le tenant courant a la creation de l'EntityManager.
 */
@SpringBootTest
class TenantIsolationTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    private TransactionTemplate tx;

    private final UUID tenantA = UUID.randomUUID();
    private final UUID tenantB = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        this.tx = new TransactionTemplate(transactionManager);
    }

    @AfterEach
    void cleanUp() {
        tx.executeWithoutResult(status -> {
            entityManager.createNativeQuery("DELETE FROM memberships WHERE tenant_id IN (UUID_TO_BIN(?1), UUID_TO_BIN(?2))")
                    .setParameter(1, tenantA.toString())
                    .setParameter(2, tenantB.toString())
                    .executeUpdate();
        });
    }

    @Test
    void unTenantNeVoitJamaisLesMembershipsDUnAutreTenant() {
        Role role = tx.execute(status -> roleRepository.findAll().get(0));

        Long membershipAId = createMembership(tenantA, 1001L, role);
        Long membershipBId = createMembership(tenantB, 1002L, role);

        runAsTenant(tenantA, () -> {
            assertThat(membershipRepository.findById(membershipAId)).isPresent();
            assertThat(membershipRepository.findById(membershipBId)).isEmpty();

            List<Membership> visibles = membershipRepository.findAll();
            assertThat(visibles).extracting(Membership::getId).containsExactly(membershipAId);
            return null;
        });
    }

    @Test
    void sansTenantDansLeContexteAucuneLigneNestVisible() {
        Role role = tx.execute(status -> roleRepository.findAll().get(0));
        createMembership(tenantA, 1001L, role);

        tx.executeWithoutResult(status -> assertThat(membershipRepository.findAll()).isEmpty());
    }

    private Long createMembership(UUID tenantId, Long userId, Role role) {
        return runAsTenant(tenantId, () -> {
            Membership membership = new Membership();
            membership.setUserId(userId);
            membership.setRole(role);
            membership.setStatus(MembershipStatus.ACTIVE);
            return membershipRepository.save(membership).getId();
        });
    }

    private <T> T runAsTenant(UUID tenantId, Supplier<T> action) {
        TenantContext.set(tenantId);
        try {
            TransactionCallback<T> callback = status -> action.get();
            return tx.execute(callback);
        } finally {
            TenantContext.clear();
        }
    }
}
