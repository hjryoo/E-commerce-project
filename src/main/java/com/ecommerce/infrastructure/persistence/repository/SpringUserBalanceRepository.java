package com.ecommerce.infrastructure.persistence.repository;

import com.ecommerce.infrastructure.persistence.entity.UserBalanceJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Spring Data JPA Repository
 * JPA 엔티티에만 의존하는 Infrastructure 전용 레포지토리
 */
public interface SpringUserBalanceRepository extends JpaRepository<UserBalanceJpaEntity, Long> {

    /**
     * 사용자 ID로 잔액 조회
     */
    Optional<UserBalanceJpaEntity> findByUserId(Long userId);

    /**
     * 사용자 ID로 잔액 조회 (비관적 락 적용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ub FROM UserBalanceJpaEntity ub WHERE ub.userId = :userId")
    Optional<UserBalanceJpaEntity> findByUserIdWithLock(@Param("userId") Long userId);

    /**
     * 사용자 존재 여부 확인
     */
    boolean existsByUserId(Long userId);
}
