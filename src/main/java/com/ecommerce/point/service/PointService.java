package com.ecommerce.point.service;
import com.ecommerce.point.entity.PointHistory;
import com.ecommerce.point.entity.UserBalance;
import com.ecommerce.point.exception.UserNotFoundException;
import com.ecommerce.point.repository.PointHistoryRepository;
import com.ecommerce.point.repository.UserBalanceRepository;
import com.ecommerce.point.service.dto.ChargePointCommand;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PointService {

    private final UserBalanceRepository userBalanceRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointService(UserBalanceRepository userBalanceRepository,
                        PointHistoryRepository pointHistoryRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.pointHistoryRepository = pointHistoryRepository;
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

        // 2. 포인트 충전
        userBalance.charge(command.amount());

        // 3. 잔액 저장
        UserBalance savedBalance = userBalanceRepository.save(userBalance);

        // 4. 히스토리 기록
        PointHistory history = PointHistory.createChargeHistory(
                command.userId(),
                command.amount(),
                savedBalance.getBalance(),
                command.description() != null ? command.description() : "포인트 충전"
        );
        pointHistoryRepository.save(history);

        return savedBalance;
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
     * 사용자 잔액 엔티티 조회
     */
    public UserBalance getUserBalanceEntity(Long userId) {
        return userBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }

    /**
     * 포인트 사용 (주문/결제 모듈에서 사용)
     */
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public UserBalance usePoint(Long userId, BigDecimal amount, String description) {
        // 1. 사용자 잔액 조회
        UserBalance userBalance = userBalanceRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 포인트 사용
        userBalance.use(amount);

        // 3. 잔액 저장
        UserBalance savedBalance = userBalanceRepository.save(userBalance);

        // 4. 히스토리 기록
        PointHistory history = PointHistory.createUseHistory(
                userId,
                amount,
                savedBalance.getBalance(),
                description != null ? description : "포인트 사용"
        );
        pointHistoryRepository.save(history);

        return savedBalance;
    }

    /**
     * 포인트 히스토리 조회
     */
    public List<PointHistory> getPointHistory(Long userId) {
        return pointHistoryRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }
}