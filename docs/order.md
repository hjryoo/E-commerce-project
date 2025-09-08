```
src/main/java/com/ecommerce/order/
├── 📁 domain/                           # 도메인 계층 (핵심 비즈니스 로직)
│   ├── 📁 model/                        # 도메인 모델
│   │   ├── Order.java                   # 주문 도메인 객체
│   │   ├── OrderItem.java               # 주문 상품 객체
│   │   ├── Payment.java                 # 결제 도메인 객체
│   │   └── OrderStatus.java             # 주문 상태 enum
│   ├── 📁 port/                         # 포트 인터페이스
│   │   ├── 📁 in/                       # Inbound Port (유스케이스)
│   │   │   ├── PlaceOrderUseCase.java   # 주문 생성 유스케이스
│   │   │   └── ProcessPaymentUseCase.java # 결제 처리 유스케이스
│   │   └── 📁 out/                      # Outbound Port
│   │       ├── OrderRepository.java     # 주문 저장소 포트
│   │       ├── UserRepository.java      # 사용자 저장소 포트
│   │       ├── ProductRepository.java   # 상품 저장소 포트
│   │       ├── PaymentGateway.java      # 결제 게이트웨이 포트
│   │       └── EventPublisher.java      # 이벤트 발행 포트
│   └── 📁 service/                      # 도메인 서비스
│       ├── OrderDomainService.java      # 주문 도메인 서비스
│       └── PaymentDomainService.java    # 결제 도메인 서비스
├── 📁 application/                      # 애플리케이션 계층 (유스케이스 구현)
│   ├── 📁 service/                      # 애플리케이션 서비스
│   │   ├── PlaceOrderService.java       # 주문 생성 서비스
│   │   └── ProcessPaymentService.java   # 결제 처리 서비스
│   └── 📁 dto/                          # 애플리케이션 DTO
│       ├── PlaceOrderCommand.java       # 주문 생성 커맨드
│       ├── OrderResult.java             # 주문 결과
│       └── PaymentResult.java           # 결제 결과
├── 📁 adapter/                          # 어댑터 계층 (외부 인터페이스)
│   ├── 📁 in/                           # Inbound Adapter
│   │   └── 📁 web/                      # 웹 어댑터
│   │       ├── OrderController.java     # REST 컨트롤러
│   │       └── dto/                     # 웹 DTO
│   │           ├── CreateOrderRequest.java
│   │           └── OrderResponse.java
│   └── 📁 out/                          # Outbound Adapter
│       ├── 📁 persistence/              # 영속성 어댑터
│       │   ├── OrderJpaEntity.java      # JPA 엔티티
│       │   ├── OrderItemJpaEntity.java  # 주문상품 JPA 엔티티
│       │   ├── SpringDataOrderRepository.java
│       │   └── OrderPersistenceAdapter.java
│       ├── 📁 external/                 # 외부 시스템 어댑터
│       │   ├── PaymentGatewayAdapter.java
│       │   └── DataPlatformAdapter.java
│       └── 📁 event/                    # 이벤트 어댑터
│           └── OrderEventPublisher.java
└── 📁 config/                           # 설정
    └── OrderConfig.java                 # 의존성 주입 설정
```