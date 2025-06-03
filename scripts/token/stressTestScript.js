import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const BASE_URL = 'http://host.docker.internal:8080/queue/token';

// stress Test(점진적 과부하)
export const options = {
    scenarios: {
        sc01: { executor: 'constant-vus', exec: 'scenario01', vus: 10, duration: '30s' },
        sc02: { executor: 'constant-vus', exec: 'scenario02', vus: 50, duration: '30s', startTime: '30s' },
        sc03: { executor: 'constant-vus', exec: 'scenario03', vus: 100, duration: '30s', startTime: '60s' },
        sc04: { executor: 'constant-vus', exec: 'scenario04', vus: 300, duration: '30s', startTime: '90s' },
    }
};

function makeRequest() {
    const payload = JSON.stringify({
        userId: __VU.toString(), // VU 번호를 문자열로
        concertId: 1,
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    let res = http.post(BASE_URL, payload, params);

    if (res.status !== 200) {
        console.error(`❌ Failed with status ${res.status}: ${res.body}`);
    }

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}

export function scenario01() {
    makeRequest();
}
export function scenario02() {
    makeRequest();
}
export function scenario03() {
    makeRequest();
}
export function scenario04() {
    makeRequest();
}

export function handleSummary(data) {
    const totalRequests = data.metrics.http_reqs.values.count;
    const totalTime = data.state.testRunDurationMs / 1000;
    const totalTPS = totalRequests / totalTime;

    console.log(`\n📊 TPS (전체 평균): ${totalTPS.toFixed(2)} TPS`);
    console.log(`🧪 총 요청 수: ${totalRequests}`);
    console.log(`⏱️ 전체 테스트 시간: ${totalTime.toFixed(2)}초`);

    return {
        "summary.html": htmlReport(data),
    };
}