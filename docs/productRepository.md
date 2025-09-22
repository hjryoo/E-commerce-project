````
src/main/java/com/ecommerce/
├── 📁 domain/                           # Domain Layer
│   ├── model/                           # 순수 도메인 모델
│   │   └── Product.java
│   └── port/out/                        # Outbound Port
│       └── ProductRepository.java       # 도메인 포트 인터페이스
├── 📁 infrastructure/                   # Infrastructure Layer
│   └── persistence/                     # 영속성 구현
│       ├── entity/                      # JPA 전용 엔티티
│       │   └── ProductJpaEntity.java
│       ├── repository/                  # Spring Data JPA Repository
│       │   └── SpringProductRepository.java
│       └── adapter/                     # Persistence Adapter
│           └── ProductPersistenceAdapter.java
└── 📁 application/                      # Application Layer
└── service/
└── ProductService.java
````