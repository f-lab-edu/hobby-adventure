import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const imageFetchDuration = new Trend('image_fetch_duration');

export const options = {
    scenarios: {
        image_load: {
            executor: 'shared-iterations',
            vus: 50,
            iterations: 2000,
            maxDuration: '5m',
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

export default function (data) {
    const url = data.urls[Math.floor(Math.random() * data.urls.length)];
    const res = http.get(url);
    imageFetchDuration.add(res.timings.duration);
    check(res, { 'is status 200': (r) => r.status === 200 });
}
