package com.ecommerce.point.repository;

import com.ecommerce.point.entity.UserBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

    // 사용자 ID로 잔액 조회
    Optional<UserBalance> findByUserId(Long userId);

    // 사용자 ID로 잔액 조회 (비관적 락 적용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ub FROM UserBalance ub WHERE ub.userId = :userId")
    Optional<UserBalance> findByUserIdWithLock(@Param("userId") Long userId);

    // 사용자 존재 여부 확인
    boolean existsByUserId(Long userId);
}