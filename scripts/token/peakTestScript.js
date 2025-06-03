import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const BASE_URL = 'http://host.docker.internal:8080/queue/token';

// Peak Test(최고 부하 테스트)
export const options = {
    scenarios: {
        steady_before_spike: {
            executor: 'constant-arrival-rate',
            rate: 50,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 100,
            exec: 'makeRequest',
        },
        spike: {
            executor: 'constant-arrival-rate',
            rate: 1000,
            timeUnit: '1s',
            duration: '10s',
            startTime: '30s',
            preAllocatedVUs: 1500,
            exec: 'makeRequest',
        },
        steady_after_spike: {
            executor: 'constant-arrival-rate',
            rate: 50,
            timeUnit: '1s',
            duration: '20s',
            startTime: '40s',
            preAllocatedVUs: 100,
            exec: 'makeRequest',
        }
    }
}

export function makeRequest() {
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

export function peakScenario() {
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