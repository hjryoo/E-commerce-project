package com.ecommerce.domain.port.out;

import com.ecommerce.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 도메인 포트 - 상품 영속성 계약
 * 도메인이 외부 영속성 레이어에 의존하는 추상 인터페이스
 */
public interface ProductRepository {

    /**
     * 상품 저장
     */
    Product save(Product product);

    /**
     * ID로 상품 조회
     */
    Optional<Product> findById(Long id);

    /**
     * ID로 상품 조회 (배타적 락 적용)
     */
    Optional<Product> findByIdWithLock(Long id);

    /**
     * 활성 상품 전체 조회
     */
    List<Product> findAllActiveProducts();

    /**
     * 활성 상품 페이징 조회
     */
    Page<Product> findActiveProducts(Pageable pageable);

    /**
     * 카테고리별 활성 상품 조회
     */
    List<Product> findActiveProductsByCategory(String category);

    /**
     * 상품명으로 검색
     */
    List<Product> findByNameContaining(String keyword);

    /**
     * 복수 ID로 상품 조회
     */
    List<Product> findByIds(List<Long> ids);

    /**
     * 복수 ID로 상품 조회 (배타적 락 적용)
     */
    List<Product> findByIdsWithLock(List<Long> ids);

    /**
     * 상품 존재 여부 확인
     */
    boolean existsById(Long id);

    /**
     * 상품 삭제
     */
    void delete(Product product);

    /**
     * 전체 상품 수 조회
     */
    long count();

    /**
     * 활성 상품 수 조회
     */
    long countActiveProducts();
}
