package com.jian.hobbyadventure.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 값입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
