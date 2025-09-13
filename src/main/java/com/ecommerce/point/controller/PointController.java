package com.ecommerce.point.controller;
import com.ecommerce.point.controller.dto.BalanceResponse;
import com.ecommerce.point.controller.dto.ChargePointRequest;
import com.ecommerce.point.controller.dto.PointResponse;
import com.ecommerce.point.entity.UserBalance;
import com.ecommerce.point.service.PointService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * 포인트 충전 API
     */
    @PostMapping("/charge")
    public ResponseEntity<PointResponse> chargePoint(@RequestBody @Valid ChargePointRequest request) {
        UserBalance userBalance = pointService.chargePoint(request.toCommand());
        PointResponse response = PointResponse.from(userBalance);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 잔액 조회 API
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long userId) {
        BigDecimal balance = pointService.getUserBalance(userId);
        BalanceResponse response = BalanceResponse.of(userId, balance);
        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 히스토리 조회 API (선택사항)
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getPointHistory(@PathVariable Long userId) {
        var history = pointService.getPointHistory(userId);
        return ResponseEntity.ok(history);
    }
}