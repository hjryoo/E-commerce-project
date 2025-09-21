````
src/main/java/com/ecommerce/
├── 📁 domain/                           # Domain Layer
│   ├── model/                           # 순수 도메인 모델
│   │   └── UserBalance.java
│   └── port/out/                        # Outbound Port
│       └── UserBalanceRepository.java   # 도메인 포트 인터페이스
├── 📁 infrastructure/                   # Infrastructure Layer
│   └── persistence/                     # 영속성 구현
│       ├── entity/                      # JPA 전용 엔티티
│       │   └── UserBalanceJpaEntity.java
│       ├── repository/                  # Spring Data JPA Repository
│       │   └── SpringUserBalanceRepository.java
│       └── adapter/                     # Persistence Adapter
│           └── UserBalancePersistenceAdapter.java
└── 📁 application/                      # Application Layer
└── service/
└── PointService.java
````