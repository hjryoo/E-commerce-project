package com.ecommerce.infrastructure.persistence.repository;

import com.ecommerce.infrastructure.persistence.entity.ProductJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository
 * JPA 엔티티에만 의존하는 Infrastructure 전용 레포지토리
 */
public interface SpringProductRepository extends JpaRepository<ProductJpaEntity, Long> {

    /**
     * ID로 상품 조회 (비관적 락 적용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.id = :id")
    Optional<ProductJpaEntity> findByIdWithLock(@Param("id") Long id);

    /**
     * 활성 상품 전체 조회
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.active = true ORDER BY p.name")
    List<ProductJpaEntity> findAllActiveProducts();

    /**
     * 활성 상품 페이징 조회
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.active = true")
    Page<ProductJpaEntity> findActiveProducts(Pageable pageable);

    /**
     * 카테고리별 활성 상품 조회
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.category = :category AND p.active = true ORDER BY p.name")
    List<ProductJpaEntity> findActiveProductsByCategory(@Param("category") String category);

    /**
     * 상품명으로 검색 (활성 상품만)
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.name LIKE %:keyword% AND p.active = true ORDER BY p.name")
    List<ProductJpaEntity> findByNameContaining(@Param("keyword") String keyword);

    /**
     * 복수 ID로 상품 조회
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.id IN :ids")
    List<ProductJpaEntity> findByIds(@Param("ids") List<Long> ids);

    /**
     * 복수 ID로 상품 조회 (비관적 락 적용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.id IN :ids")
    List<ProductJpaEntity> findByIdsWithLock(@Param("ids") List<Long> ids);

    /**
     * 활성 상품 수 조회
     */
    @Query("SELECT COUNT(p) FROM ProductJpaEntity p WHERE p.active = true")
    long countActiveProducts();
}
