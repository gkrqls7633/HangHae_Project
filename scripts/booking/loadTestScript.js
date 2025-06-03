import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const BASE_URL = 'http://host.docker.internal:8080/bookings/seats';
const TOKEN = 'ff16156b-1563-4620-93a3-bcf51f435b5b';

// Load Test(부하 테스트)
export const options = {
    scenarios: {
        load_test: {
            executor: 'constant-vus',
            vus: 100,                 // 실제 사용 예상치 기반
            duration: '5m',
            exec: 'loadScenario',
        }
    }
}

function makeRequest() {
    const payload = JSON.stringify({
        userId: 1, // VU 번호를 문자열로
        concertId: 1,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`,
        },
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

export function loadScenario() {
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