# 항해 6주차 과제

## 목차
[1. 배경](#1-배경)

[2. 문제 해결](#2-문제-해결)

[3. 결론](#3-결론)


# ✏️ 1. 배경

콘서트 예매 시스템에서 빠른 매진 랭킹을 Redis 기반으로 개발하고 설계 및 구현한다.

사용자는 인기 콘서트를 실시간으로 확인할 수 있다.

기존의 RDBMS 기반 설계에서는 다음과 같은 문제가 존재한다.

- 빠르게 변동되는 예약 수를 매번 DB에 반영하면 **쓰기 성능 병목** 발생
- 인기 랭킹 조회 시 정렬 및 집계 비용이 커져서 **조회 응답 지연**
- 트래픽이 몰리는 시점에 동시 요청 처리가 어려움

따라서 빠른 응답성과 실시간 순위 정렬을 제공하기 위해, **Redis 기반의 캐시 시스템**으로 해당 기능을 구현한다.

## 📌 1.1 Redis 자료구조

Redis 는 기본적으로 Key-Value 기반의 데이터 저장소이다.

Value 로는 단순히 String 뿐 아니라 **다양한 데이터 타입 ( Collections )**를 지원하는데, 이를 활용해 단순히 캐시 뿐 아니라 다양한 용도로 활용한다.

[redis_자료구조](redis_자료구조.png)

---

# ✏️  2. 문제 해결

## 📌 2-1. Redis를 선택한 이유

- **고속 읽기/쓰기 처리 능력**: Redis는 메모리 기반 구조로 빠른 응답 속도를 보장한다.
- **TTL 지원**: 콘서트의 유효기간에 따라 데이터 자동 만료가 가능하다.
- **정렬 구조 제공**: `Sorted Set` 자료구조를 통해 실시간 정렬된 데이터를 간단하게 관리할 수 있다.(이외에도 다양한 자료 구조를 사용하기 용이하다.)

## 📌 2-2. Sorted Set(ZSet)을 택한 이유

Redis의 `Sorted Set`은 다음과 같은 특성으로 빠른 매진 랭킹에 적합하다.

- **값과 점수를 분리하여 저장**: 콘서트 ID를 값으로, 예매 횟수를 점수(score)로 저장
- **자동 정렬**: 예매 횟수가 증가할수록 자동으로 높은 순위에 위치
- **범위 조회**: 상위 N개의 콘서트를 빠르게 조회 가능 (`ZREVRANGE`)

## 📌 2-3. 구현 프로세스 Design

### (1) 예매 성공 시 스코어 증가

- 콘서트 ID를 키로 하여 `ZINCRBY` 명령으로 점수 증가
- 예시: `ZINCRBY concert:ranking 1 concertId`

| Java 메서드 | Redis 명령어 |
| --- | --- |
| `concertRankingKey` (`String`) | `concert:ranking` (ZSet key 이름) |
| `concertId` (`String`) | `concertId` (ZSet의 member 값) |
| `increment` (`double`) | `1` (score 증가값) |

```java
/**
 * @description RedisTemplate 활용하여 redis에 콘서트 랭킹 스코어를 저장합니다.
 *  - sorted set 자료구조로 저장
 *  - 예약 성공 시 score + 1
 * @param concertId 콘서트 ID
 */
@Override
public void incrementConcertBookingScore(String concertId) {

    redisTemplate.opsForZSet().incrementScore(concertRankingKey, concertId, increment);
}

```

### (2) 콘서트별 TTL 설정 - 채택x

- 실제 예약 가능 시간(콘서트 시작 시간)까지만 랭킹에 유지되도록 TTL 정책 적용
- 초기엔 개별 키로 관리하려 했지만, 전체 랭킹이 하나의 `ZSet`으로 관리되어야 하므로, 스코어 증가 시 TTL 설정은 제거
- 대신, **스케줄러 기반 정리 방식 채택**

### (3) 콘서트 만료 처리 (스케줄러) - 채택

- 콘서트 시작 시간이 지난 경우, `ZSet`에서 해당 ID를 제거
- `concertStartDate`가 현재 시간보다 이전인 콘서트를 DB에서 조회
- 존재 여부 확인 후 Redis에서 삭제 (`ZREM`)

| Java 메서드 | Redis 명령어 |
| --- | --- |
| `redisTemplate.opsForZSet().remove("concert:ranking", concertId);` | `ZREM concert:ranking concertId` |

```java
 /**
 * @description redis에서 콘서트 매진 랭킹 sorted set의 특정 concertId를 제거한다.
 */
@Override
public void cleanExpiredConcerts(String concertId) {
    // Redis ZSet에서 해당 concertId의 순위를 확인
    Long rank = redisTemplate.opsForZSet().rank("concert:ranking", concertId);

    // 만약 해당 concertId가 ZSet에 존재하면
    if (rank != null) {
        redisTemplate.opsForZSet().remove("concert:ranking", concertId);
    }
}
```

---

# ✏️  3. 결론

## 📌 3-1. 구현 결과

Redis + Sorted Set 기반의 빠른 매진 랭킹 시스템 구현 결과

- ✔ 실시간 인기 순위 반영 가능
- ✔ 빠른 조회 및 높은 동시 처리 성능
- ✔ 콘서트 유효 시간 이후 자동 정리 처리 가능 (스케줄러 기반)
- ✔ 백엔드 시스템 부하 분산 및 사용자 경험 향상

> 이 설계는 추후 시스템에 더 많은 사용자와 예약 요청이 몰리더라도 성능 저하 없이 처리할 수 있도록 확장할 수 있으며, TTL 설정이나 이벤트 기반 처리 방식(예: Kafka 등)과 연동하면 예약 종료 시점이나 콘서트 상태 변화에 따라 랭킹 데이터를 더욱 유연하고 자동적으로 관리할 수 있다..

## 📌 3-2. 보완 사항

- 일간/주간 랭킹 분리하여 각각 키 구성 전략을 다르게 가져가 볼 수 있을 것.
- Sorted Set에 너무 많은 데이터가 들어가게 되면 메모리 부담이 증가하므로 또 다른 TTL 설정 고려.


[캐시 전략 및 성능 개선 보고서 notion](https://www.notion.so/teamsparta/ranking-design-1f12dc3ef514807faba8fc342e951867#1f12dc3ef5148076b5d3f116d9d48a8c)
