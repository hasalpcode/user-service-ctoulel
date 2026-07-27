package com.hasalp.ctoulel_user_service.config;

import com.hasalp.ctoulel_user_service.security.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateTenantConfig {

    @Bean
    public HibernatePropertiesCustomizer tenantIdentifierResolverCustomizer(TenantIdentifierResolver resolver) {
        return properties -> properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
    }
}
