import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';

const imageFetchDuration = new Trend('image_fetch_duration');
const TESTID = __ENV.TESTID;
if (!TESTID) {
    throw new Error('TESTID 환경변수가 설정되지 않았습니다. TESTID=... 형태로 실행해주세요.');
}

export const options = {
    scenarios: {
        stage1: {
            executor: 'shared-iterations',
            vus: 5,
            iterations: 100,
            maxDuration: '3m',
            startTime: '0s',
            exec: 'loadImage',
        },
        stage2: {
            executor: 'shared-iterations',
            vus: 20,
            iterations: 200,
            maxDuration: '5m',
            startTime: '3m',
            exec: 'loadImage',
        },
        stage3: {
            executor: 'shared-iterations',
            vus: 50,
            iterations: 300,
            maxDuration: '6m',
            startTime: '8m',
            exec: 'loadImage',
        },
    },
};

export function setup() {
    const urls = [];
    for (let page = 1; page <= 10; page++) {
        const res = http.get(`http://nginx/api/v1/explorations?page=${page}&size=10`);
        const body = JSON.parse(res.body);
        body.data.forEach((item) => {
            if (item.thumbnailUrl) urls.push(item.thumbnailUrl);
        });
    }
    if (urls.length === 0) {
        throw new Error('썸네일 URL을 하나도 못 가져옴 - 데이터 확인 필요');
    }
    return { urls };
}

export function loadImage(data) {
    const url = data.urls[Math.floor(Math.random() * data.urls.length)];
    const res = http.get(url, { timeout: '120s' });
    imageFetchDuration.add(res.timings.duration);
    check(res, { 'is status 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
    return {
        stdout: textSummary(data, { indent: ' ', enableColors: true }),
        [`/scripts/results/summary-${TESTID}.json`]: JSON.stringify(data, null, 2),
    };
}
