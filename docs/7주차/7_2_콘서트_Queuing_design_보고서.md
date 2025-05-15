# 항해 6주차 과제

## 목차
[1. 배경](#1-배경)

[2. 문제 해결](#2-문제-해결)

[3. 결론](#3-결론)


# ✏️ 1. 배경

콘서트 예매 시스템에서 기존 RDBMS 기반 대기열 시스템을 Redis 기반으로 개발하고 설계 및 구현한다.

기존의 RDBMS 기반 설계에서는 다음과 같은 문제가 존재한다.

- 빠르게 변동되는 대기열 토큰 발급, 만료 처리를 매번 DB에 반영하면 **쓰기 성능 병목** 발생
- 트래픽이 몰리는 시점에 동시 요청 처리가 어려움

따라서 빠른 응답성과 실시간 대기열 처리를 제공하기 위해, **Redis key-value NoSQL DB를 활용한다.**

---

# ✏️ 2. 문제 해결

## 📌 2-1. Redis를 선택한 이유

- **빠른 읽기/쓰기 속도**: 메모리 기반 DB로, 초당 수천~수만 건의 트래픽에도 성능 저하 없이 동작(조회 성능은 RDBMS에 비해 훨등히 빠름)
- **다양한 자료구조 지원**: Strings, Hash, Set, ZSet 등 다양한 자료구조를 통해 유연한 데이터 표현 가능
- **TTL, 만료 정책 지원**: 자동 만료 기능을 이용해 불필요한 데이터 클리어 가능

## 📌 2-2. 자료구조 pick

Redis의 주요 자료구조를 다음과 같이 활용하고자 한다.

| 역할 | 자료구조 | Key 형태 | 목적 |
| --- | --- | --- | --- |
| 토큰 상세정보 저장 | Hash | `token:{tokenValue}` | user_id, 토큰 상태, 발급 시간, 만료 시간 저장 |
| 유저-토큰 맵핑 | String (Key-Value) | `user:{userId}:token` | 유저가 가진 현재 토큰 식별 |
| 대기열 우선순위 큐 | Sorted Set (ZSET) | `queue:global` | 발급 시간 또는 만료 시간 기준 정렬하여 상태 전이 컨트롤 |

<토큰 상세 정보>

| Key | 예시 |
| --- | --- |
| `token:{token_value}` | `token:`e9f9d6ce-0db8-4809-bead-6553f19ca51e
|
| Fields (`Hash`) | `user_id`, `token_status`, `issued_at`, `expired_at` |

[유저_토큰값](유저_토큰값.png)

장점:

- Redis에 토큰 상태/시간을 명확하게 구조화
- 갱신/읽기 모두 빠름

**<**유저-토큰 맵핑**>**

| Key | Value |
| --- | --- |
| `user:{user_id}:token` | `{token_value}` |

[토큰_매핑정보](토큰_매핑정보.png)

장점:

- 유저 ID로 토큰 빠르게 조회 가능
- 토큰 중복 발급/재발급 체크에도 유용

**<**대기열 우선순위 큐**>**

| Key | Member | Score |
| --- | --- | --- |
| `queue:global` | `"token:{token_value}"` | `expired_at (epochMillis)` |

[sorted Set 대기열](대기열_토큰_sortedSet.png)

[sorted Set 대기열_score](대기열_토큰_sortedSet_withscores.png)


장점:

- 발급 시간 기준으로 자동 정렬
- `range`, `rank`, `score` 등으로 상태 전이 컨트롤 용이

## 📌 2-3. 구현 프로세스

[Queuing_디자인구조](콘서트시스템_레디스기반_설계.png)

🔹2-3-1. 토큰 발급

- 유저는 대기열 토큰 발행 요청하고, Queue System을 통해 Ready 토큰을 발급받는다.

🔹2-3-2. 토큰 활성화 : 대기열 진입 (`READY` → `ACTIVE`)

- **Scheduler 기반** `queue:global` 에서 `expired_at > now`(만료시간이 도래되지 않은) 인 토큰 조회하여`ACTIVE`로 상태를 변경한다.
- Active 토큰을 기반으로 유저는 콘서트 좌석 예약이 가능하다.

🔹2-3-3. 토큰 만료 처리

- `ACTIVE` 상태의 토큰 중 `expired_at < now`(만료시간이 도래된) 인 토큰을 찾아 `EXPIRED`로 상태 전이
- 이때 Redis에서는: 만료된 데이터를 제거한다.
    - 해당 Hash 삭제 (`token:{token_value}`)
    - 유저-토큰 맵핑 삭제 (`user:{user_id}:token`)
    - ZSet (`queue:global`)에서도 해당 토큰 제거

---

# ✏️ 3. 결론

## 📌 3-1. 구현 결과

Redis와 Sorted Set 기반 대기열 시스템 도입 결과,

✔ **실시간 대기열 순위 반영 가능**

✔ **빠른 조회 및 높은 동시 처리 성능 확보**

✔ **토큰 만료 시간 기반 자동 정리 처리 가능 (스케줄러 활용)**

✔ **DB 부하 감소 및 사용자 응답 속도 향상**

> 본 설계는 향후 더 많은 사용자와 트래픽 증가에도 성능 저하 없이 확장 가능하며, TTL 설정 또는 Kafka와 같은 이벤트 스트리밍 시스템과 연동 시 더욱 유연한 상태 관리가 가능하다.
>

---

## 📌 3-2. 보완 사항

- 대기열 구성 시 queue:global 전역 대기열이 아닌 콘서트별 queue:concert_id 형태로 구성할 필요가 있다.
- **임의로 셋팅 되어있는 스케줄러 스펙이 아닌, TPS 계산 ( API QPS, DB CONNPOOL, REDIS RPS, …), 부하테스트 등을 기반으로 토큰 전이 스케줄러 스펙을 결정할 필요가 있다.**
[캐시 전략 및 성능 개선 보고서 notion](https://www.notion.so/teamsparta/ranking-design-1f12dc3ef514807faba8fc342e951867#1f12dc3ef5148076b5d3f116d9d48a8c)
