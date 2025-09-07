package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 재고가 있는 상품만 조회
    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.name")
    List<Product> findAvailableProducts();

    // 상품명으로 검색
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% ORDER BY p.name")
    List<Product> findByNameContaining(String name);

    // 가격 범위로 검색
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price")
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}