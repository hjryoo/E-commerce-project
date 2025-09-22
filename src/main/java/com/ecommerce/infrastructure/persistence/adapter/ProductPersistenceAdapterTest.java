package com.ecommerce.infrastructure.persistence.adapter;

import com.ecommerce.domain.model.Product;
import com.ecommerce.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.infrastructure.persistence.repository.SpringProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private SpringProductRepository springRepository;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    @DisplayName("상품 저장 - 신규 생성")
    void save_NewProduct_Success() {
        // Given
        Product product = Product.create("테스트 상품", "설명",
                BigDecimal.valueOf(10000), 100, "전자제품");

        ProductJpaEntity savedEntity = new ProductJpaEntity(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
        savedEntity.setId(1L);
        savedEntity.setVersion(0L);

        when(springRepository.save(any(ProductJpaEntity.class)))
                .thenReturn(savedEntity);

        // When
        Product result = adapter.save(product);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(result.getStock()).isEqualTo(100);

        verify(springRepository).save(any(ProductJpaEntity.class));
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void findById_ExistingProduct_ReturnsProduct() {
        // Given
        Long productId = 1L;
        ProductJpaEntity entity = new ProductJpaEntity(
                "테스트 상품", "설명", BigDecimal.valueOf(10000), 100,
                "전자제품", true, LocalDateTime.now(), LocalDateTime.now()
        );
        entity.setId(productId);

        when(springRepository.findById(productId))
                .thenReturn(Optional.of(entity));

        // When
        Optional<Product> result = adapter.findById(productId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(productId);
        assertThat(result.get().getName()).isEqualTo("테스트 상품");
    }

    @Test
    @DisplayName("활성 상품 목록 조회")
    void findAllActiveProducts_ReturnsActiveProducts() {
        // Given
        List<ProductJpaEntity> entities = List.of(
                createProductEntity(1L, "상품1", true),
                createProductEntity(2L, "상품2", true)
        );

        when(springRepository.findAllActiveProducts())
                .thenReturn(entities);

        // When
        List<Product> result = adapter.findAllActiveProducts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).isActive()).isTrue();
    }

    private ProductJpaEntity createProductEntity(Long id, String name, boolean active) {
        ProductJpaEntity entity = new ProductJpaEntity(
                name, "설명", BigDecimal.valueOf(10000), 100,
                "전자제품", active, LocalDateTime.now(), LocalDateTime.now()
        );
        entity.setId(id);
        return entity;
    }
}
