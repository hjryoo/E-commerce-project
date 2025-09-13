package com.ecommerce.point.service;

import com.ecommerce.point.entity.UserBalance;
import com.ecommerce.point.exception.InsufficientBalanceException;
import com.ecommerce.point.exception.InvalidAmountException;
import com.ecommerce.point.repository.PointHistoryRepository;
import com.ecommerce.point.repository.UserBalanceRepository;
import com.ecommerce.point.service.dto.ChargePointCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 충전 - 신규 사용자")
    void chargePoint_NewUser_Success() {
        // Given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(10000);
        ChargePointCommand command = new ChargePointCommand(userId, chargeAmount, "테스트 충전");

        when(userBalanceRepository.findByUserIdWithLock(userId))
                .thenReturn(Optional.empty());
        when(userBalanceRepository.save(any(UserBalance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserBalance result = pointService.chargePoint(command);

        // Then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualByComparingTo(chargeAmount);

        verify(userBalanceRepository).findByUserIdWithLock(userId);
        verify(userBalanceRepository).save(any(UserBalance.class));
        verify(pointHistoryRepository).save(any());
    }

    @Test
    @DisplayName("포인트 충전 - 기존 사용자")
    void chargePoint_ExistingUser_Success() {
        // Given
        Long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(5000);
        BigDecimal chargeAmount = BigDecimal.valueOf(10000);
        BigDecimal expectedBalance = BigDecimal.valueOf(15000);

        UserBalance existingBalance = UserBalance.create(userId, initialBalance);
        ChargePointCommand command = new ChargePointCommand(userId, chargeAmount, "테스트 충전");

        when(userBalanceRepository.findByUserIdWithLock(userId))
                .thenReturn(Optional.of(existingBalance));
        when(userBalanceRepository.save(any(UserBalance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserBalance result = pointService.chargePoint(command);

        // Then
        assertThat(result.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("포인트 사용 - 잔액 부족 시 예외 발생")
    void usePoint_InsufficientBalance_ThrowsException() {
        // Given
        Long userId = 1L;
        BigDecimal currentBalance = BigDecimal.valueOf(5000);
        BigDecimal useAmount = BigDecimal.valueOf(10000);

        UserBalance userBalance = UserBalance.create(userId, currentBalance);

        when(userBalanceRepository.findByUserIdWithLock(userId))
                .thenReturn(Optional.of(userBalance));

        // When & Then
        assertThatThrownBy(() -> pointService.usePoint(userId, useAmount, "테스트 사용"))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("잔액이 부족합니다");
    }
}
