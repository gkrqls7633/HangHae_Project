# 항해 플러스 2주차 과제

## 목차
- [문제 정의 및 요구사항 분석](#문제-정의-및-요구사항-분석)
  - [문제 정의](#문제-정의)
  - [요구사항 분석](#요구사항-분석)
- [시나리오 분석 및 작업 계획](#시나리오-분석-및-작업-계획)


## 문제 정의 및 요구사항 분석
### 문제 정의
- 사용자 경험 최적화 및 시스템의 안정성과 신뢰성을 보장하는 콘서트 예약 서비스를 구축한다.
- 해당 서비스는 사용자 경험을 최적화하면서 시스템의 안정성과 신뢰성을 보장해야 한다. 여러 유저가 동시에 이용할 수 있도록 대기열 시스템, 잔액 관리, 결제 시스템, 좌석 예약 시스템을 안정적으로 처리해야 하며, 사용자는 미리 충전한 잔액으로 좌석을 예약하고, 결제가 즉시 이루어지지 않더라도 일정 시간 동안 다른 유저가 해당 좌석에 접근하지 못하도록 해야 한다.

### 요구사항 분석
<기능적 요구사항>

2-1. 유저 토큰 발급 API
- 유저가 서비스에 접근할 수 있도록 고유한 토큰을 발급한다.
- 토큰은 유저의 UUID와 해당 유저의 대기열을 관리할 수 있는 정보(대기 순서 or 잔여 시간 등)를 표함한다.
- 이후 모든 API는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능하다.

2-2. 예약 가능 날짜 / 좌석 확인 API
- 유저가 예약 가능한 날짜 및 좌석 정보를 조회할 수 있어야 한다.
- 실시간으로 좌석 예약 상태를 확인하고, 예약 가능한 좌석을 제공해야 한다.
- 좌석 정보는 1~50 까지의 좌석번호로 관리된다.

2-3.좌석 예약 요청 API
- 유저가 선택한 좌석을 예약 요청할 수 있어야 한다.
- 예약 요청 시 잔액 확인을 통해 사용자의 예약 가능 여부를 결정한다.
- 예약 시, 결제가 즉시 이루어지지 않더라도 일정 시간 동안 다른 유저가 해당 좌석을 예약할 수 없도록 보호한다.(시간은 정책에 따라 자율적으로 정의)

2-4. 잔액 충전 조회 API
- 유저가 잔액을 충전하거나 조회할 수 있는 기능을 제공한다.
- 충전된 잔액은 좌석 예약에 사용되며, 잔액을 실시간으로 확인할 수 있어야 한다.

2-5.결제 API
- 예약이 완료되면 결제가 이루어져야 하며, 결제 후 좌석을 예약 완료 상태로 처리하고 결제 내역을 생성한다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만려시킨다.

<비기능적 요구사항>
1. 성능
- 동시성 처리 : 여러 유저가 동시에 예약 요청을 시도할 수 있으므로, 동시성 문제를 해결하기 위해 락(lock) 또는 트랜잭션을 활용한다.
- 예약 시스템은 실시간 응답을 제공하며, 실시간 좌석 예약 가능 상태를 확인할 수 있어야 한다.

2. 확장성
- 서비스는 수평 확장이 가능해야 하며, 서버나 DB의 트래픽이 증가할 경우 서버를 추가하여 대응할 수 있어야 한다.
- 분산 캐시를 활용하여 여러 인스턴스 간 상태를 공유하고 성능 저하를 방지할 수 있어야 한다.

3. 안정성
- 장애 처리 : 결제 실패나 서버 장애가 발생한 경우, 유저에게 적절한 오류 메시지를 반환하고, 장애 복구가 가능하도록 시스템을 설계해야 한다.
- 트랜잭션 관리 : 좌석 예약과 결제는 원자적으로 처리되어야 하며, 중간에 문제가 발생한 경우 시스템이 복구 가능해야 한다.

4. 보안
- 유저 인증 및 인증된 토큰 관리를 통해 유저의 데이터를 보호하고, 불법 접근을 막아야 한다.
- 암호화된 결제 처리를 통해 유저 결제 정보를 보호한다.

5. 대기열 시스템
- 예약 대기열 시스템을 통해 유저가 동시에 예약하려는 좌석에 대해 대기할 수 있도록 하며, 대기 중인 유저는 순차적으로 예약을 시도할 수 있다.
- 대기열에 유저가 많은 경우 예상 시간을 안내하는 기능도 고려한다.

## 시나리오 분석 및 작업 계획
- [마일스톤](docs/마일스톤_Gannt.jpg)
- [유스케이스 다이어그램](docs/콘서트예약시스템_유스케이스.png)
- [시퀀스 다이어그램](docs/콘서트티켓예약서비스_시퀀스다이어그램.png)
- [클래스 다이어그램]()
  
