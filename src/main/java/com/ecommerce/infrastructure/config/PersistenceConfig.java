package com.ecommerce.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableRetry
@EnableJpaRepositories(basePackages = "com.ecommerce.infrastructure.persistence.repository")
public class PersistenceConfig {
    // JPA 및 트랜잭션 설정
}