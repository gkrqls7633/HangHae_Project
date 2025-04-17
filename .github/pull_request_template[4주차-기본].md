# 4주차 기본과제


### 각 시나리오별 비즈니스 로직 개발 및 단위테스트 작성
0. 보충
- commit 
  - 775531e : 콘서트예약시스템_ERD다이어그램, 시퀀스다이어그램 설계 변경 및 보강

1. Infrastructure Layer 작성
- https://github.com/users/gkrqls7633/projects/2/views/5?pane=issue&itemId=105184147&issue=gkrqls7633%7CHangHae_Project%7C16
- commit
  - 6a32969 : infrastructure Repository 구현체 및 Custom 인터페이스 구성

2. 기능별 통합 테스트 작성
- commit 
  - b30e460 : PointService 통합테스트 작성
  - 9a6fd6b, 0bbcc26 : BookingService 통합테스트 작성
  - 9d1777b : PaymentService 통합테스트 작성

3. 트랜잭션 개념 & ACID 원칙 이해
- https://github.com/gkrqls7633/HangHae_Project/issues/36
- commit
  - 6ce925f :트랜잭션 개념 및 특징 정리

4. 트랜잭션 격리 수준 & 동시성 이슈 시나리오 검증
- https://github.com/gkrqls7633/HangHae_Project/issues/37
- commit
  - 4cde523 : 격리 수준 별 테스트 시나리오 구현
  - b0b7b44 : READ_COMMITTED 테스트 시나리오 구현 및 단위테스트 작성
  - 03f9644 : REPEATABLE_READ 격리 수준 테스트 시나리오 구현 및 단위테스트 작성
  - 46dbd28 : Serializabled 격리 수준 테스트 시나리오 구현 및 단위테스트 작성
  - d0f2fc7 : Transactional option 개념 작성



---
### **리뷰 포인트(질문)**
- 리뷰 포인트 1
  - 기존 설계는 Payment와 Booking이 연관관계를 맺고 있었는데, 고민해보니 꼭 예약을 한다고 결제를 하는건 아니라는 판단을 했고, 연관관계를 제거했습니다. 이부분에 대해 코치님의 의견이 궁금합니다.

- 리뷰 포인트 2
  - 현재 대기열 토큰의 대기상태 -> 활성화 상태를 스케줄을 변경하도록 처리했습니다. 해당 스케줄 시간 정책을 tps를 기반으로 셋팅한다고 하셨는데, 지금 수준(부하테스트를 하지 않은 상황)에서도 명확한 시간 셋팅이 가능한지 궁금합니다.

---
### **이번주 KPT 회고**
- 통합테스트, 단위테스트 명확히 구분하기 / 잘 된 DB설계는 점차 개선해 나가는 것
### Keep
- 테스트 주도 개발 계속 해보자.
### Problem
- DB설계를 명확하게 했어야했다. 다시 수정한 부분인 생각보다 있다.
### Try
- 격리 수준 별 Read 이슈에 대해 이해하기.