package com.ecommerce.product.service;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = Product.create("테스트 상품", new BigDecimal("10000"), 100);
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다")
    void getAllProducts_ShouldReturnAllProducts() {
        // given
        List<Product> expectedProducts = Collections.singletonList(sampleProduct);
        given(productRepository.findAll()).willReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.getAllProducts();

        // then
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getName()).isEqualTo("테스트 상품");
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("ID로 상품을 조회할 수 있다")
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.of(sampleProduct));

        // when
        Product actualProduct = productService.getProductById(productId);

        // then
        assertThat(actualProduct.getName()).isEqualTo("테스트 상품");
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회시 예외가 발생한다")
    void getProductById_ShouldThrowException_WhenProductNotExists() {
        // given
        Long nonExistentId = 999L;
        given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품입니다. ID: " + nonExistentId);

        verify(productRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("재고가 있는 상품만 조회할 수 있다")
    void getAvailableProducts_ShouldReturnProductsWithStock() {
        // given
        List<Product> expectedProducts = Collections.singletonList(sampleProduct);
        given(productRepository.findAvailableProducts()).willReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.getAvailableProducts();

        // then
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getStock()).isGreaterThan(0);
        verify(productRepository).findAvailableProducts();
    }

    @Test
    @DisplayName("상품명으로 검색할 수 있다")
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // given
        String searchName = "테스트";
        List<Product> expectedProducts = Collections.singletonList(sampleProduct);
        given(productRepository.findByNameContaining(searchName)).willReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.searchProductsByName(searchName);

        // then
        assertThat(actualProducts).hasSize(1);
        assertThat(actualProducts.get(0).getName()).contains(searchName);
        verify(productRepository).findByNameContaining(searchName);
    }

    @Test
    @DisplayName("가격 범위로 상품을 검색할 수 있다")
    void getProductsByPriceRange_ShouldReturnProductsInRange() {
        // given
        BigDecimal minPrice = new BigDecimal("5000");
        BigDecimal maxPrice = new BigDecimal("15000");
        List<Product> expectedProducts = Collections.singletonList(sampleProduct);
        given(productRepository.findByPriceBetween(minPrice, maxPrice)).willReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.getProductsByPriceRange(minPrice, maxPrice);

        // then
        assertThat(actualProducts).hasSize(1);
        verify(productRepository).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    @DisplayName("최소가격이 최대가격보다 클 때 예외가 발생한다")
    void getProductsByPriceRange_ShouldThrowException_WhenMinPriceGreaterThanMaxPrice() {
        // given
        BigDecimal minPrice = new BigDecimal("20000");
        BigDecimal maxPrice = new BigDecimal("10000");

        // when & then
        assertThatThrownBy(() -> productService.getProductsByPriceRange(minPrice, maxPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최소 가격은 최대 가격보다 클 수 없습니다.");
    }
}
