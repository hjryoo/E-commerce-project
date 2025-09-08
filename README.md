# 🛒 E-Commerce Service

> **확장 가능한 전자상거래 서비스**
> 
> DOCS
 - [상품조회](docs/viewProduct.md)
 - [주문결제](docs/order.md)

## 🏗️ 아키텍처 설계

### 계층 구조 (Clean + Layered Architecture)

```
┌─────────────────────────────────────┐
│        Presentation Layer           │ ← Controller, DTO
├─────────────────────────────────────┤
│        Application Layer            │ ← UseCase, Service
├─────────────────────────────────────┤
│           Domain Layer              │ ← Entity, Repository Interface
├─────────────────────────────────────┤
│       Infrastructure Layer          │ ← Repository Impl, External API
└─────────────────────────────────────┘
```

### 🔄 의존성 방향

```
Presentation → Application → Domain ← Infrastructure
```

## 🛠️ 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: H2 (개발용), MySQL (운영용)
- **ORM**: JPA/Hibernate
- **Cache**: Redis
- **Build Tool**: Gradle
- **Test**: JUnit 5, Mockito, TestContainers

## 📁 프로젝트 구조

```
src/main/java/com/ecommerce/
├── interfaces/           # 표현 계층
│   └── web/
│       ├── controller/   # REST Controllers
│       └── dto/          # Request/Response DTOs
├── application/          # 응용 계층
│   ├── usecase/          # 비즈니스 유스케이스
│   └── service/          # 응용 서비스
├── domain/              # 도메인 계층 (핵심)
│   ├── model/           # 도메인 엔티티
│   ├── repository/      # 리포지토리 인터페이스
│   └── service/         # 도메인 서비스
└── infrastructure/       # 인프라 계층
    ├── persistence/     # 데이터베이스 구현
    ├── external/        # 외부 API 연동
    └── config/          # 설정 클래스
```

## ⚡ 주요 기능

### 1. 💰 잔액 관리
- **잔액 충전**: 사용자별 포인트 충전 기능
- **잔액 조회**: 실시간 잔액 확인

### 2. 📦 상품 관리
- **상품 조회**: 실시간 재고 정보 포함
- **재고 관리**: 동시성 이슈 고려한 안전한 재고 차감

### 3. 🛒 주문/결제 (클린 아키텍처 적용)
- **주문 생성**: 다중 상품 주문 지원
- **결제 처리**: 잔액 기반 결제 시스템
- **데이터 전송**: 외부 데이터 플랫폼 연동

### 4. 🎫 선착순 쿠폰
- **쿠폰 발급**: 선착순 한정 쿠폰 발급
- **쿠폰 조회**: 사용자별 보유 쿠폰 목록

### 5. 📊 인기 상품 추천
- **통계 집계**: 최근 3일간 판매량 기준
- **상위 5개 상품**: 실시간 인기 상품 제공

## 🔍 API 명세

| 기능 | Method | Endpoint | 설명 |
|------|--------|----------|------|
| 잔액 충전 | `POST` | `/users/{userId}/balance/charge` | 사용자 잔액 충전 |
| 잔액 조회 | `GET` | `/users/{userId}/balance` | 사용자 잔액 조회 |
| 상품 목록 조회 | `GET` | `/products` | 전체 상품 목록 |
| 상품 상세 조회 | `GET` | `/products/{productId}` | 특정 상품 정보 |
| 주문 생성 | `POST` | `/orders` | 새 주문 생성 및 결제 |
| 쿠폰 발급 | `POST` | `/coupons/{couponId}/issue` | 선착순 쿠폰 발급 |
| 쿠폰 조회 | `GET` | `/users/{userId}/coupons` | 사용자 보유 쿠폰 |
| 인기 상품 | `GET` | `/products/popular` | 인기 상품 TOP 5 |
