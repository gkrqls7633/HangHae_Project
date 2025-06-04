import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export const options = {
    scenarios: {
        get_concert_list: {
            executor: 'constant-vus',
            exec: 'getConcertList',
            vus: 3000,
            duration: '1m',
            startTime: '0s',
        },
        test_flow: {
            executor: 'constant-vus',
            exec: 'testFlow',
            vus: 1500,
            duration: '1m',
            startTime: '10s',
        },
    },
};

const BASE = 'http://host.docker.internal:8080';


export function getConcertList() {
    const res = http.get(`${BASE}/concerts/list`);
    check(res, { '✅ GET /concerts/list 성공': (r) => r.status === 200 });
    sleep(10);
}

export function issueToken() {
    const userId = Math.floor(Math.random() * 100) + 1;
    const concertId = Math.floor(Math.random() * 10) + 1;

    console.log(`🎟️ 발급 요청 - userId: ${userId}, concertId: ${concertId}`);

    const payload = JSON.stringify({
        userId,
        concertId,
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(`${BASE}/queue/token`, payload, params);
    check(res, { '✅ POST /queue/token 성공': (r) => r.status === 200 });

    //10초 대기
    sleep(10);

    return { userId, concertId };

}

export function getUserQueueToken(userId) {
    const url = `${BASE}/users/token?userId=${userId}`;
    const res = http.get(url);
    check(res, { '✅ GET /users/token 성공': (r) => r.status === 200 });
    sleep(1);

    const data = res.json();
    console.log(`🎫 token: ${data.data.tokenValue}, userId: ${data.data.userId}`);

    return data.data.tokenValue;  // tokenValue 반환
}

export function bookSeat(token, userId, concertId) {
    const payload = JSON.stringify({
        userId,
        concertId,
    });

    console.log(`🎫 좌석 예약 요청 - userId: ${userId}, concertId: ${concertId}, token: ${token}`);

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.post(`${BASE}/bookings/seats`, payload, params);
    check(res, { '✅ POST /bookings/seats 성공': (r) => r.status === 200 });
}

// export function makePayment() {
//     const payload = JSON.stringify({
//         bookingId: 1,
//         userId: 1,
//     });
//
//     const params = {
//         headers: { 'Content-Type': 'application/json' },
//     };
//
//     const res = http.post(`${BASE}/payments`, payload, params);
//     check(res, { '✅ POST /payments 성공': (r) => r.status === 200 });
//
//     sleep(10);
// }

export function testFlow() {
    const { userId, concertId } = issueToken();  // 토큰 발급과 user/concert id 반환
    const token = getUserQueueToken(userId);  // 토큰 획득
    if (!token) {
        console.error('토큰을 받지 못했습니다!');
        return;
    }
    bookSeat(token, userId, concertId);
    // makePayment();
}

export function handleSummary(data) {
    const totalRequests = data.metrics.http_reqs.values.count;
    const totalFailures = data.metrics.checks.values.fails; // 실패 수
    const totalSuccesses = data.metrics.checks.values.passes; // 성공 수
    const totalTime = data.state.testRunDurationMs / 1000;
    const totalTPS = totalRequests / totalTime;

    console.log(`\n📊 TPS (전체 평균): ${totalTPS.toFixed(2)} TPS`);
    console.log(`🧪 총 요청 수: ${totalRequests}`);
    console.log(`✅ 성공 수: ${totalSuccesses}`);
    console.log(`❌ 실패 수: ${totalFailures}`);
    console.log(`⏱️ 전체 테스트 시간: ${totalTime.toFixed(2)}초`);

    return {
        "summary.html": htmlReport(data),
    };
}
