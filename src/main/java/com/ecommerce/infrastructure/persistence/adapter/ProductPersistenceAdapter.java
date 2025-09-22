package com.ecommerce.infrastructure.persistence.adapter;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.ProductRepository;
import com.ecommerce.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.infrastructure.persistence.repository.SpringProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence Adapter - Outbound Port 구현체
 * 도메인 포트를 구현하여 실제 JPA 작업을 수행
 * 도메인 ↔ JPA 엔티티 간 매핑 책임
 */
@Component
public class ProductPersistenceAdapter implements ProductRepository {

    private final SpringProductRepository springRepository;

    public ProductPersistenceAdapter(SpringProductRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = toJpaEntity(product);
        ProductJpaEntity savedEntity = springRepository.save(entity);

        // ID와 Version을 도메인 객체에 역주입
        product.assignId(savedEntity.getId());
        product.assignVersion(savedEntity.getVersion());

        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springRepository.findById(id)
                .map(this::toDomainModel);
    }

    @Override
    public Optional<Product> findByIdWithLock(Long id) {
        return springRepository.findByIdWithLock(id)
                .map(this::toDomainModel);
    }

    @Override
    public List<Product> findAllActiveProducts() {
        return springRepository.findAllActiveProducts().stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> findActiveProducts(Pageable pageable) {
        return springRepository.findActiveProducts(pageable)
                .map(this::toDomainModel);
    }

    @Override
    public List<Product> findActiveProductsByCategory(String category) {
        return springRepository.findActiveProductsByCategory(category).stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String keyword) {
        return springRepository.findByNameContaining(keyword).stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        return springRepository.findByIds(ids).stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByIdsWithLock(List<Long> ids) {
        return springRepository.findByIdsWithLock(ids).stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return springRepository.existsById(id);
    }

    @Override
    public void delete(Product product) {
        if (product.getId() != null) {
            springRepository.deleteById(product.getId());
        }
    }

    @Override
    public long count() {
        return springRepository.count();
    }

    @Override
    public long countActiveProducts() {
        return springRepository.countActiveProducts();
    }

    /**
     * 도메인 모델 → JPA 엔티티 변환 (Infrastructure 책임)
     */
    private ProductJpaEntity toJpaEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );

        // 업데이트인 경우 ID와 Version 설정
        if (product.getId() != null) {
            entity.setId(product.getId());
            entity.setVersion(product.getVersion());
        }

        return entity;
    }

    /**
     * JPA 엔티티 → 도메인 모델 변환 (Infrastructure 책임)
     */
    private Product toDomainModel(ProductJpaEntity entity) {
        return Product.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getCategory(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}