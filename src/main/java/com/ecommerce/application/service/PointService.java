package com.ecommerce.application.service;

import com.ecommerce.application.dto.ChargePointCommand;
import com.ecommerce.domain.model.UserBalance;
import com.ecommerce.domain.port.out.UserBalanceRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Application Service
 * 도메인 포트에만 의존하여 비즈니스 유스케이스 수행
 */
@Service
@Transactional(readOnly = true)
public class PointService {

    private final UserBalanceRepository userBalanceRepository; // 도메인 포트 의존

    public PointService(UserBalanceRepository userBalanceRepository) {
        this.userBalanceRepository = userBalanceRepository;
    }

    /**
     * 포인트 충전
     */
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public UserBalance chargePoint(ChargePointCommand command) {
        // 1. 사용자 잔액 조회 또는 생성
        UserBalance userBalance = userBalanceRepository.findByUserIdWithLock(command.userId())
                .orElseGet(() -> UserBalance.create(command.userId()));

        // 2. 포인트 충전 (도메인 로직)
        userBalance.charge(command.amount());

        // 3. 저장 (Infrastructure에 위임)
        return userBalanceRepository.save(userBalance);
    }

    /**
     * 잔액 조회
     */
    public BigDecimal getUserBalance(Long userId) {
        return userBalanceRepository.findByUserId(userId)
                .map(UserBalance::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 포인트 사용 (주문/결제 모듈에서 사용)
     */
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public UserBalance usePoint(Long userId, BigDecimal amount) {
        // 1. 사용자 잔액 조회
        UserBalance userBalance = userBalanceRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 포인트 사용 (도메인 로직)
        userBalance.use(amount);

        // 3. 저장 (Infrastructure에 위임)
        return userBalanceRepository.save(userBalance);
    }
}