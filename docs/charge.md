```
src/main/java/com/ecommerce/point/
├── 📁 controller/                       # Presentation Layer
│   ├── PointController.java             # REST API 컨트롤러
│   └── dto/                             # Web DTO
│       ├── ChargePointRequest.java      # 포인트 충전 요청 DTO
│       ├── PointResponse.java           # 포인트 응답 DTO
│       └── BalanceResponse.java         # 잔액 조회 응답 DTO
├── 📁 service/                          # Business Layer
│   ├── PointService.java               # 포인트 비즈니스 로직
│   └── dto/                             # Service DTO
│       └── ChargePointCommand.java     # 서비스 계층 커맨드
├── 📁 repository/                       # Data Access Layer
│   ├── UserBalanceRepository.java      # 사용자 잔액 레포지토리
│   └── PointHistoryRepository.java     # 포인트 히스토리 레포지토리
├── 📁 entity/                           # Domain/Data Model
│   ├── UserBalance.java                # 사용자 잔액 엔티티
│   └── PointHistory.java               # 포인트 히스토리 엔티티
├── 📁 exception/                        # Exception Handling
│   ├── InsufficientBalanceException.java
│   ├── UserNotFoundException.java
│   └── InvalidAmountException.java
└── 📁 config/                           # Configuration
└── PointConfig.java                # 설정 클래스
```