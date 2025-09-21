package com.ecommerce.infrastructure.persistence.adapter;

import com.ecommerce.domain.model.UserBalance;
import com.ecommerce.domain.port.out.UserBalanceRepository;
import com.ecommerce.infrastructure.persistence.entity.UserBalanceJpaEntity;
import com.ecommerce.infrastructure.persistence.repository.SpringUserBalanceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence Adapter - Outbound Port 구현체
 * 도메인 포트를 구현하여 실제 JPA 작업을 수행
 * 도메인 ↔ JPA 엔티티 간 매핑 책임
 */
@Component
public class UserBalancePersistenceAdapter implements UserBalanceRepository {

    private final SpringUserBalanceRepository springRepository;

    public UserBalancePersistenceAdapter(SpringUserBalanceRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public UserBalance save(UserBalance userBalance) {
        UserBalanceJpaEntity entity = toJpaEntity(userBalance);
        UserBalanceJpaEntity savedEntity = springRepository.save(entity);

        // ID와 Version을 도메인 객체에 역주입
        userBalance.assignId(savedEntity.getId());
        userBalance.assignVersion(savedEntity.getVersion());

        return userBalance;
    }

    @Override
    public Optional<UserBalance> findByUserId(Long userId) {
        return springRepository.findByUserId(userId)
                .map(this::toDomainModel);
    }

    @Override
    public Optional<UserBalance> findByUserIdWithLock(Long userId) {
        return springRepository.findByUserIdWithLock(userId)
                .map(this::toDomainModel);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return springRepository.existsByUserId(userId);
    }

    @Override
    public Optional<UserBalance> findById(Long id) {
        return springRepository.findById(id)
                .map(this::toDomainModel);
    }

    @Override
    public void delete(UserBalance userBalance) {
        if (userBalance.getId() != null) {
            springRepository.deleteById(userBalance.getId());
        }
    }

    /**
     * 도메인 모델 → JPA 엔티티 변환 (Infrastructure 책임)
     */
    private UserBalanceJpaEntity toJpaEntity(UserBalance userBalance) {
        UserBalanceJpaEntity entity = new UserBalanceJpaEntity(
                userBalance.getUserId(),
                userBalance.getBalance(),
                userBalance.getCreatedAt(),
                userBalance.getUpdatedAt()
        );

        // 업데이트인 경우 ID와 Version 설정
        if (userBalance.getId() != null) {
            entity.setId(userBalance.getId());
            entity.setVersion(userBalance.getVersion());
        }

        return entity;
    }

    /**
     * JPA 엔티티 → 도메인 모델 변환 (Infrastructure 책임)
     */
    private UserBalance toDomainModel(UserBalanceJpaEntity entity) {
        return UserBalance.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getBalance(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}