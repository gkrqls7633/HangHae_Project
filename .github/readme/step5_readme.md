# [step5] 각 시나리오별 하기 비즈니스 로직 개발 및 단위 테스트 작성

## 📦 프로젝트 구조 소개
- 본 프로젝트는 클린 아키텍처의 원칙을 반영한 레이어드 아키텍처 기반의 구조로 구성되어 있으며,
  핵심 도메인을 중심으로 관심사 분리를 통해 유지보수성과 확장성을 높이는 것을 목표로 합니다.

## 📁 디렉토리 구조

<pre><code>
src/ 
├── interface/ # 입출력 계층 (Controller, DTO)
│├── controller/ # HTTP 요청 처리 
│ └── dto/ # 외부와 주고받는 데이터 구조 
│ 
├── application/ # 유즈케이스 계층 (Facade 역할) 
│ └── XxxFacade.java 
│ 
├── service/ # 핵심 도메인 비즈니스 로직 
│ └── XxxService.java 
│ 
├── domain/ # 도메인 모델 계층 
│ ├── model/ # 엔티티, 밸류 객체 등 
│ └── repository/ # 도메인 관점의 추상 저장소 인터페이스 
│ 
├── infrastructure/ # 외부 시스템 연동 및 기술 구현 
│ ├── repository/ # JPA, Redis 등 실제 저장소 구현체 
│ └── external/ # 외부 API 클라이언트 등

</code></pre>

## 💡 아키텍처 설계 원칙
✅ 레이어드 아키텍처 기반
- 상위 계층이 하위 계층을 호출하는 단방향 흐름
- 각 계층은 명확한 책임을 가짐 (입출력, 유즈케이스, 도메인, 인프라)

✅ 클린 아키텍처 철학 반영
- 도메인 로직은 외부와 분리되어 순수하게 유지
- 의존성 역전(DIP): 도메인이 외부 기술에 의존하지 않음
- 트랜잭션 흐름 및 유즈케이스 조정은 application 계층에서 담당
- 외부 시스템(JPA, 외부 API 등)은 infrastructure 계층에 격리

## 🧭 계층 간 역할 요약
### 계층	역할
- interface	: HTTP 요청/응답 처리 및 DTO 구성
- application : 유즈케이스 흐름 조정, 트랜잭션 관리 (Facade 역할)
- service : 실제 도메인 비즈니스 로직 구현
- domain : 핵심 모델과 도메인 규칙 정의
- infrastructure : DB, 외부 API 등 기술 구현 상세 처리

✅ 장점
- 관심사 분리로 변경에 유연
- 테스트 용이: 도메인 계층은 외부 기술에 독립
- 유스케이스 단위 확장 쉬움: application에서 조립
- 실무에 적합한 현실적인 구조