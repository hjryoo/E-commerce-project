package com.ecommerce.application.service;

import com.ecommerce.application.dto.CreateProductCommand;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.ProductRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service
 * 도메인 포트에만 의존하여 비즈니스 유스케이스 수행
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository; // 도메인 포트 의존

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 생성
     */
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = Product.create(
                command.name(),
                command.description(),
                command.price(),
                command.stock(),
                command.category()
        );

        return productRepository.save(product);
    }

    /**
     * 상품 조회
     */
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));
    }

    /**
     * 활성 상품 목록 조회 (페이징)
     */
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findActiveProducts(pageable);
    }

    /**
     * 카테고리별 상품 조회
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findActiveProductsByCategory(category);
    }

    /**
     * 상품 검색
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }

    /**
     * 재고 감소 (주문 시 사용)
     */
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    /**
     * 재고 증가 (주문 취소 시 사용)
     */
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.increaseStock(quantity);
        productRepository.save(product);
    }

    /**
     * 상품 활성화
     */
    @Transactional
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.activate();
        productRepository.save(product);
    }

    /**
     * 상품 비활성화
     */
    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.deactivate();
        productRepository.save(product);
    }

    /**
     * 복수 상품 조회 (주문 시 사용)
     */
    public List<Product> getProducts(List<Long> productIds) {
        return productRepository.findByIds(productIds);
    }

    /**
     * 복수 상품 조회 (락 적용)
     */
    public List<Product> getProductsWithLock(List<Long> productIds) {
        return productRepository.findByIdsWithLock(productIds);
    }
}