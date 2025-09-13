package com.ecommerce.point.repository;

import com.ecommerce.point.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // 사용자별 포인트 히스토리 조회 (페이징)
    Page<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 사용자별 최근 히스토리 조회
    List<PointHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 기간의 히스토리 조회
    List<PointHistory> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
