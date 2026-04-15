package com.jian.hobbyadventure.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 값입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");

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
