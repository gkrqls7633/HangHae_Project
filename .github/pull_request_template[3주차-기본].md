# 3주차 기본과제


### 각 시나리오별 비즈니스 로직 개발 및 단위테스트 작성
1. 클린&레이어드 아키텍처 비교 및 도메인 모델 분리 계획 수립
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184147&issue=gkrqls7633%7CHangHae_Project%7C16

2. 도메인&애플리케이션 계층 설계
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184151&issue=gkrqls7633%7CHangHae_Project%7C17

3. 데이터 계층 구현 및 외부 의존성 분리
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184160&issue=gkrqls7633%7CHangHae_Project%7C18
- commit
  - 1d36992 : 데이터 계층 구현 및 외부 의존성 분리
  - a920ae5 : UserController부터 UserRepository 까지 외부 → 내부 의존 흐름 점검 (Mock api)

4. 콘서트 조회 비즈니스 로직 개발 및 단위 테스트 작성
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184166&issue=gkrqls7633%7CHangHae_Project%7C19
- commit
  - 2a11522 : 콘서트 목록 조회 api mock 생성
  - 273837f : 콘서트 조회 mock api 추가(- 특정 콘서트의 예약 가능한 좌석 조회 (랜덤으로 좌석 상태 50개 지정) -> 제외
  - 651a7f1 : 콘서트 조회 기능 단위테스트 추가

5. 콘서트 예약/결제 기능 비즈니스 로직 개발 및 단위 테스트 작성
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184173&issue=gkrqls7633%7CHangHae_Project%7C20
- commit
  - 5ad0b41 : 콘서트 예약 시 해당 좌석의 예약 가능 여부 체크
  - 989a7db : 좌석 예약 요청 mock api
  - dcccf7b : 결제 요청 비즈니스 로직 추가
  - 745ac4d : 결제 포인트 잔액과 콘서트가격 비교 로직 추가
  - d324f3d : PayRequest 결제 요청 DTO sample value 작성
  - 7b2b7af : 결제 시 Point 차감 및 단위테스트 추가
  - b4f6a40 : 잔액 차감 save 호출 추가

6. 포인트 충전 기능 비즈니스 로직 개발 및 단위 테스트 작성
- https://github.com/gkrqls7633/HangHae_Project/issues/21
- commit
  - bfdd5c8 : 유저 포인트 조회 및 충전 기능 추가

---
### **리뷰 포인트(질문)**
- 리뷰 포인트 1
dcccf7b(결제 요청 비즈니스 로직 추가)에서 facade패턴 활용하여 예약 + 결제를 한 트랜잭션으로 처리하기 위해 BookingPaymentFacade를 만들어두었습니다.
또한 예약과 결제가 서로 분리된 작업으로(분리된 트랜잭션)으로 처리될 수도 있다고 생각하여 분리된 각각의 서비스를 작성해두었습니다.
두 방식을 모두 고려해야하는지, 혹은 정책을 fix하고 하나에만 집중하면 될지 궁금합니다.

- 리뷰 포인트 2
bfdd5c8(포인트 충전 로직): jpa를 사용해본 적이 없어 어려움을 겪었는데, 해당 로직 순서가 맞는지 궁금합니다.
포인트충전 시 유저의 기본 Point 정보 조회 -> Payment 도메인의 충전 로직으로 충전금액 set -> 해당 충전금액 영속화 -> db save
이렇게 생각하여 로직을 짜보았는데, 맞는지 궁금합니다.

---
### **이번주 KPT 회고**
- 테스트 주도 개발에 대해 감이 슬슬 오는 거 같기도 하고..?
### Keep
- 테스트 주도 개발 계속 해보자.
- ### Problem
- jpa를 사용해 본 적이 없어서 쿼리메서드 사용에 어려움을 겪음.
### Try
- jpa 공부하자.