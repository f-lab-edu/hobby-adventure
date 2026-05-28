import http from 'k6/http';
import { check } from 'k6';
import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.1.0/index.js";

export let options = {
    vus: 300,
    duration: '10m',
};

export default function () {
    const userId = randomIntBetween(1, 100);
    const page = randomIntBetween(1, 10);
    const size = 10;

    const params = {
        headers: { 'X-User-Id': String(userId) },
    };

    let response = http.get(
        `http://spring:8080/api/v1/my-explorations?page=${page}&size=${size}`,
        params
    );

    check(response, {
        'is status 200': (r) => r.status === 200,
    });
}
