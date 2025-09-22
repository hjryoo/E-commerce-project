package com.ecommerce.domain.model;

/**
 * 순수 도메인 객체 - JPA 어노테이션 없음
 * 상품의 비즈니스 로직과 상태를 담당
 */
public class Product {

    private Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private int stock;
    private final String category;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version; // 낙관적 락을 위한 버전 필드

    // 생성자
    private Product(String name, String description, BigDecimal price, int stock,
                    String category, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // 정적 팩토리 메서드
    public static Product create(String name, String description, BigDecimal price, int stock, String category) {
        validateCreateParams(name, price, stock);
        return new Product(name, description, price, stock, category, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    // 재구성을 위한 팩토리 메서드 (Infrastructure에서 사용)
    public static Product reconstitute(Long id, String name, String description, BigDecimal price,
                                       int stock, String category, boolean active,
                                       LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        Product product = new Product(name, description, price, stock, category, active, createdAt, updatedAt);
        product.id = id;
        product.version = version;
        return product;
    }

    // 비즈니스 로직 - 재고 감소
    public void decreaseStock(int quantity) {
        validateDecreaseStock(quantity);
        if (this.stock < quantity) {
            throw new IllegalStateException(
                    String.format("재고가 부족합니다. 현재 재고: %d, 요청 수량: %d", this.stock, quantity)
            );
        }
        this.stock -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 - 재고 증가
    public void increaseStock(int quantity) {
        validateIncreaseStock(quantity);
        this.stock += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 - 상품 활성화
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 - 상품 비활성화
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 - 주문 가능 여부 확인
    public boolean isOrderable(int quantity) {
        return this.active && this.stock >= quantity;
    }

    // 비즈니스 로직 - 총 가격 계산
    public BigDecimal calculateTotalPrice(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        return this.price.multiply(BigDecimal.valueOf(quantity));
    }

    // 검증 로직
    private static void validateCreateParams(String name, BigDecimal price, int stock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }

    private void validateDecreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("감소할 재고 수량은 0보다 커야 합니다.");
        }
    }

    private void validateIncreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("증가할 재고 수량은 0보다 커야 합니다.");
        }
    }

    // ID 할당 (Infrastructure 전용 - package-private)
    void assignId(Long id) {
        this.id = id;
    }

    void assignVersion(Long version) {
        this.version = version;
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}