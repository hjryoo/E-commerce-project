package com.ecommerce;

import com.ecommerce.application.dto.CreateProductCommand;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_Success() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
                "테스트 상품", "상품 설명", BigDecimal.valueOf(10000), 100, "전자제품"
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.assignId(1L);
                    return product;
                });

        // When
        Product result = productService.createProduct(command);

        // Then
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(result.getStock()).isEqualTo(100);
        assertThat(result.isActive()).isTrue();

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("재고 감소 성공")
    void decreaseStock_Success() {
        // Given
        Long productId = 1L;
        int decreaseQuantity = 10;

        Product product = Product.create("테스트 상품", "설명",
                BigDecimal.valueOf(10000), 100, "전자제품");
        product.assignId(productId);

        when(productRepository.findByIdWithLock(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        productService.decreaseStock(productId, decreaseQuantity);

        // Then
        assertThat(product.getStock()).isEqualTo(90);

        verify(productRepository).findByIdWithLock(productId);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void decreaseStock_InsufficientStock_ThrowsException() {
        // Given
        Long productId = 1L;
        int decreaseQuantity = 150; // 재고보다 많은 수량

        Product product = Product.create("테스트 상품", "설명",
                BigDecimal.valueOf(10000), 100, "전자제품");
        product.assignId(productId);

        when(productRepository.findByIdWithLock(productId))
                .thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> productService.decreaseStock(productId, decreaseQuantity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }
}