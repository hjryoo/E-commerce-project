package com.ecommerce.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("상품을 정상적으로 생성할 수 있다")
    void create_ShouldCreateProduct_WhenValidInput() {
        // given
        String name = "테스트 상품";
        BigDecimal price = new BigDecimal("10000");
        Integer stock = 100;

        // when
        Product product = Product.create(name, price, stock);

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("상품명이 비어있으면 예외가 발생한다")
    void create_ShouldThrowException_WhenNameIsEmpty() {
        // given
        String emptyName = "";
        BigDecimal price = new BigDecimal("10000");
        Integer stock = 100;

        // when & then
        assertThatThrownBy(() -> Product.create(emptyName, price, stock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품명은 필수입니다.");
    }

    @Test
    @DisplayName("가격이 0 이하면 예외가 발생한다")
    void create_ShouldThrowException_WhenPriceIsZeroOrNegative() {
        // given
        String name = "테스트 상품";
        BigDecimal zeroPrice = BigDecimal.ZERO;
        Integer stock = 100;

        // when & then
        assertThatThrownBy(() -> Product.create(name, zeroPrice, stock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("재고를 정상적으로 차감할 수 있다")
    void decreaseStock_ShouldDecreaseStock_WhenSufficientStock() {
        // given
        Product product = Product.create("테스트 상품", new BigDecimal("10000"), 100);
        int decreaseAmount = 30;

        // when
        product.decreaseStock(decreaseAmount);

        // then
        assertThat(product.getStock()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고가 부족하면 차감시 예외가 발생한다")
    void decreaseStock_ShouldThrowException_WhenInsufficientStock() {
        // given
        Product product = Product.create("테스트 상품", new BigDecimal("10000"), 10);
        int decreaseAmount = 20;

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(decreaseAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다. 현재 재고: 10");
    }

    @Test
    @DisplayName("재고 가용성을 확인할 수 있다")
    void isStockAvailable_ShouldReturnTrue_WhenStockIsSufficient() {
        // given
        Product product = Product.create("테스트 상품", new BigDecimal("10000"), 100);

        // when & then
        assertThat(product.isStockAvailable(50)).isTrue();
        assertThat(product.isStockAvailable(100)).isTrue();
        assertThat(product.isStockAvailable(150)).isFalse();
    }
}