### 이슈번호

- 1. 단위 테스트 대상 기능 선정 및 레이어드 아키텍처 구조 설계(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104172514&issue=gkrqls7633%7CHangHae_Project%7C4)
  - 레이어드 아키텍처 구조 셋팅 : dcc4c39


- 2. Mock API 및 Swagger-API 코드 작성(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104172540&issue=gkrqls7633%7CHangHae_Project%7C5)
  - swagger 셋팅 : 9535d21
  - 콘서트 mock api : 9535d21
  - 좌석 예약 및 취소 mock api : 91de6b1
  - 포인트 충전 / 조회 mock api : 23e1902
  - 결제 요청 mock api : 725eb31
  - 단위 테스트 (일부; 미완) : 4ef2558, 6251c94, 4501e3f

- 3. (NiceToHave) API E2E 테스트 작성해보기(https://github.com/users/gkrqls7633/projects/2/views/1?pane=issue&itemId=104172582&issue=gkrqls7633%7CHangHae_Project%7C6)
  - 미완.
---

### **리뷰 포인트(질문)**

- 리뷰 포인트 1
  - Service 레이어를 아직 구현하지 못했습니다. Mock api라는 것이 Controller에서만 return 받을 것을 임의로 만들고 Request에 대한 response만 보기위함으로 알고있는데, Controller만 우선적으로 작성해도 무방한지 궁금합니다.
  - 당장 Service는 없으므로 호출을 검증할 수 없으니까 이렇게 될 경우 Test는 Controller 레이어만 하면 되는지 궁금합니다.
- 리뷰 포인트 2
  - JPA Datasource가 코드에 구현되어 있었는데, 최초 서버 구동 시 해당 부분에서 에러가 나 주석 처리 하여 서버 구동했습니다. 제가 체크하지 못한 부분이 있는지 궁금합니다.
  
  
---

### **이번주 KPT 회고**
- 테스트 기반 TDD가 아직 잘 안된다..
### Keep
- 확장 가능한 설계에 대해 고민하는 습관을 갖자. 언제 어떤 요구사항이 추가되고 변경될지 모른다. (내 생각이 바뀔 수도 있고)
### Problem
- TDD 좀 더 연습하자. Mock처리 그새 가물가물하다.
### Try
- TDD 관련 자료, 이전에 작성한 코드 좀 더 보자.
