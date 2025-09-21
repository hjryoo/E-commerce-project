package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.UserBalance;

import java.util.Optional;

/**
 * 도메인 포트 - 사용자 잔액 영속성 계약
 * 도메인이 외부 영속성 레이어에 의존하는 추상 인터페이스
 */
public interface UserBalanceRepository {

    /**
     * 사용자 잔액 저장
     */
    UserBalance save(UserBalance userBalance);

    /**
     * 사용자 ID로 잔액 조회
     */
    Optional<UserBalance> findByUserId(Long userId);

    /**
     * 사용자 ID로 잔액 조회 (배타적 락 적용)
     */
    Optional<UserBalance> findByUserIdWithLock(Long userId);

    /**
     * 사용자 존재 여부 확인
     */
    boolean existsByUserId(Long userId);

    /**
     * ID로 조회
     */
    Optional<UserBalance> findById(Long id);

    /**
     * 삭제
     */
    void delete(UserBalance userBalance);
}
