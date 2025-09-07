package com.ecommerce.product.service;


import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 모든 상품 조회
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 특정 상품 조회
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + id));
    }

    /**
     * 재고가 있는 상품만 조회
     */
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    /**
     * 상품명으로 검색
     */
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(name.trim());
    }

    /**
     * 가격 범위로 검색
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("999999999");

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("최소 가격은 최대 가격보다 클 수 없습니다.");
        }

        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
