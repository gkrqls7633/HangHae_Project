### 이슈번호
- 1. 서비스 시나리오 선택 및 요구사항 분석(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104170326&issue=gkrqls7633%7CHangHae_Project%7C1)
  - README.md 작성 : https://github.com/gkrqls7633/HangHae_Project/blob/hakbin/2%EC%A3%BC%EC%B0%A8-%EA%B8%B0%EB%B3%B8/README.md


- 2. 시나리오 서비스 선택(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104170967&issue=gkrqls7633%7CHangHae_Project%7C2)
    - README.md 작성 : https://github.com/gkrqls7633/HangHae_Project/blob/hakbin/2%EC%A3%BC%EC%B0%A8-%EA%B8%B0%EB%B3%B8/README.md


- 3. 추상적/구체적 설계(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104171341&issue=gkrqls7633%7CHangHae_Project%7C3)
  - 유스케이스 다이어그램 작성: 0f84cca
  - 시퀀스 다이어그램 작성 : d480568
  - 클래스 다이어그램 작성 : 8d610cf
---

### **리뷰 포인트(질문)**

- 리뷰 포인트 1
  - 로그인 처리를 다이어그램에 녹여두었습니다. 서비스 시작 조건으로 jwt 기반 회원 인증 체크를 기반으로 작성했는데, 해당 과정도 구현하는 것이 필수인지 궁금합니다. 아니면 로그인이 되어있다는 가정 하에 진행되는지 궁금합니다.
  - 대기열 토큰 발급 후 대기열 큐에 진입한 후 유저 차례에 도달하게 되면 좌석 선택 및 Booking 서비스에 접근할 수 있도록 설계했습니다. 만약 포인트가 부족한 경우 좌석 점유 후 포인트 충전 작업이 선행되어야 하는데, 
'좌석 선택'과 '포인트 충전'의 유스케이스 관계 설정을 어떻게 가져가야 할지 의문입니다. 분리해서 다른 트랜잭션(행동?)으로 보아야할지, 연결되는 일련의 한 트랜잭션(행동?)으로 보아야할지 궁금합니다.

- 리뷰 포인트 2
  - 클래스다이어그램 작성 전 도메인 객체를 먼저 생각해 보았습니다. 티켓의 유형/무형을 관리하고 이를 배송까지 할 수 있지 않을까 라는 생각에 배송 도메인도 고려해보았습니다. 배송 API를 실제 구현하진 않고 mock API 처리로 구현해도 되는지 궁금합니다.
  - 예약내역을 하나의 도메인으로 구성할 수 있을 것 같은데, 클래스다이어그램에 추가로 확정하게 된다면 Booking과 관계를 지을 수 있을까요? 아니면 유저의 예약내역 이므로 유저와의 관계를 더 갖는 것이 적절할까요?
  
---

### **이번주 KPT 회고**
- 구현/개발도 중요하지만, 앞선 요구사항 분석과 단단한 설계의 중요성을 다시금 느낄 수 있었다. 설계 많이 어렵다 ..(?)
### Keep
- 확장 가능한 설계에 대해 고민하는 습관을 갖자. 언제 어떤 요구사항이 추가되고 변경될지 모른다. (내 생각이 바뀔 수도 있고)
### Problem
- git 내역 관리를 좀 깔끔하게 잘 하자.
- 다이어그램에서 엔티티 간 관계에 대해 좀 더 깊게 생각해봐야겠다. 아직 어떤 엔티티들끼리 어떤 관계를 갖는지 명확하게 잡히지 않는다.
### Try
- DDD 관련 도서 또는 영상을 읽어봐야겠다.
