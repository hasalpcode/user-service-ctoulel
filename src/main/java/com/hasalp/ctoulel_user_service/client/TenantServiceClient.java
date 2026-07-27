package com.hasalp.ctoulel_user_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * Premier client HTTP inter-service du projet : user-service -> tenant-service,
 * pour orchestrer le signup self-service (creation du tenant, transfert de
 * propriete, abonnement). Appelle via Eureka (lb://tenant-service).
 */
@Component
public class TenantServiceClient {

    private final RestClient restClient;

    public TenantServiceClient(RestClient.Builder loadBalancedRestClientBuilder) {
        this.restClient = loadBalancedRestClientBuilder.baseUrl("lb://tenant-service").build();
    }

    public UUID createTenant(String name, String subdomain) {
        TenantCreatedResponse response = restClient.post()
                .uri("/api/tenants")
                .body(new TenantCreateRequest(name, subdomain))
                .retrieve()
                .body(TenantCreatedResponse.class);
        return response.tenantId();
    }

    public void updateOwner(UUID tenantId, Long ownerUserId) {
        restClient.patch()
                .uri("/api/tenants/{tenantId}/owner", tenantId)
                .body(new TenantOwnerUpdateRequest(ownerUserId))
                .retrieve()
                .toBodilessEntity();
    }

    public void createSubscription(UUID tenantId, UUID planId) {
        restClient.post()
                .uri("/api/subscriptions")
                .body(new SubscriptionCreateRequest(tenantId, planId))
                .retrieve()
                .toBodilessEntity();
    }

    private record TenantCreateRequest(String name, String subdomain) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TenantCreatedResponse(UUID tenantId) {}

    private record TenantOwnerUpdateRequest(Long ownerUserId) {}

    private record SubscriptionCreateRequest(UUID tenantId, UUID planId) {}
}
