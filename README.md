## 동시성(재고 정합성) 처리

### 문제
재고가 **1개인 상품**에 대해 동시에 2명이 주문(각 1개)할 때, 동시성 제어가 없으면 아래 문제가 발생할 수 있다.

- 재고가 **음수로 떨어짐**
- 주문이 중복 확정됨
- 트랜잭션 타이밍에 따라 결과가 비결정적(간헐적 실패/성공)

### 해결 전략
본 프로젝트는 **재고 정합성(음수 방지)** 을 최우선으로 두고, 재고 차감 시점에 **비관적 락(PESSIMISTIC_WRITE)** 을 사용한다.

- 재고 변경 책임은 `Item` 도메인에 캡슐화
    - `removeStock(count)`: 재고 차감 + 부족 시 예외
    - `addStock(count)`: 주문 취소 시 재고 복구
- 주문 생성 트랜잭션 내에서 재고 차감 직전에 락을 획득하여 동시 업데이트를 직렬화
- 트랜잭션을 짧게 유지하여 락 점유 시간을 최소화

### 동시성 테스트(재현/검증 방식)
멀티스레드 동시성 테스트는 “테스트 트랜잭션” 때문에 착시가 생기기 쉬워, 아래 원칙으로 구성했다.

#### 테스트 설계 원칙
1. 테스트 메서드/클래스에 `@Transactional`을 붙이지 않는다.
2. 테스트 데이터 준비는 `TransactionTemplate`로 감싸 **커밋된 상태**로 만든다.
3. 멀티스레드는 엔티티를 공유하지 않고 **ID만 공유**한다.
4. 최종 검증은 `TransactionTemplate`로 새 트랜잭션에서 조회하여 **DB 기준 값**으로 확인한다.
5. `startLatch`로 스레드를 동시에 출발시키고, `AtomicInteger`로 성공/실패 횟수를 검증한다.

#### 기대 결과
- 재고 1개에 2명이 동시에 주문 → **정확히 1건 성공, 1건 실패**
- 최종 재고는 반드시 **0**이어야 한다.

#### 테스트 코드 요약
- `startLatch`: 동시에 출발
- `doneLatch`: 모든 스레드 종료 대기
- 성공/실패 카운트: `success=1`, `fail=1` 검증
- DB 재조회: 캐시 영향 제거

## REST API (Postman 검증)

현재 프로젝트는 Postman으로 아래 흐름을 검증했다.

- 회원 생성 → 회원 목록 조회
- 상품 생성 → 상품 목록 조회
- 주문 생성 → 주문 목록 조회(주문 + 주문상품 포함)

---

### Members API

#### 1) 회원 생성
- **POST** `/api/members`

**Request**
```json
{
  "name": "user1"
} 
```

**Response 200**
```json
{
  "memberId:1"
} 
```
<img width="1124" height="845" alt="Pasted Graphic" src="https://github.com/user-attachments/assets/4ad6b74f-6fa7-4b6f-a843-682422cfdfd4" />


#### 2) 회원 중복 생성 방지
- **POST** `/api/members`
**Response409**
이미 존재하는 회원 입니다.

<img width="1116" height="847" alt="Pasted Graphic 3" src="https://github.com/user-attachments/assets/280f6ba7-8572-4708-b630-e212f7a27b19" />

#### 3) 회원 목록 조회
- **GET** `/api/members`

**Response**
```json
{
  { "id": 1, "name": "user1" },
  { "id": 2, "name": "user2" },
  { "id": 3, "name": "user3" }
}
```
<img width="1115" height="842" alt="Pasted Graphic 2" src="https://github.com/user-attachments/assets/8e57f202-a30c-4f3b-949b-85371ebcdd61" />


### Items API

#### 1) 상품 생성
- **POST** `/api/items`

**Request**
```json
{
  "name": "itemA",
  "price": 10000,
  "stockQuantity": 5
}
```
**Response**
```json
{
  "itemId:1
}
```
<img width="1121" height="847" alt="Pasted Graphic 4" src="https://github.com/user-attachments/assets/ad524603-e0db-426f-ad0a-14502c50b992" />

#### 2) 상품 목록 조회
- **GET** `/api/items`

**Response**
```json
{
 "id": 1,
    "name": "itemA",
    "price": 10000,
    "stockQuantity": 5}
```
<img width="1117" height="846" alt="Pasted Graphic 5" src="https://github.com/user-attachments/assets/29347d0f-559f-43d8-bfbd-d2433e2388ac" />


### Orders API

#### 1) 주문 생성
- **POST** `/api/orders`

**Request**
```json
{
    "memberId":1,
    "itemId:1,
    "count:1
}
```
**Response 200**
```json
{
  "orderId": 1
}
```

<img width="1120" height="845" alt="Pasted Graphic 6" src="https://github.com/user-attachments/assets/de74c455-ec6b-469d-8977-5dd36aff1e47" />

#### 2) 주문 목록 조회
- **GET** `/api/orders`

**Response 200**
```json
[
  {
    "orderId": 1,
    "memberName": "user1",
    "orderDate": "2025-12-30T17:30:43.872888",
    "status": "ORDER",
    "items": [
      {
        "itemName": "itemA",
        "price": 10000,
        "count": 1
      }
    ]
  }
]
```

### Notes
- 쓰기 작업(회원/상품/주문 생성) 은 서비스 계층에서 @Transactional로 처리하여 영속성 컨텍스트/트랜잭션 경계가 보장되도록 구성했다.
- 예외 처리(중복 회원 등)는 @RestControllerAdvice 기반으로 HTTP 상태 코드로 응답한다.
