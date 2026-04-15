package com.jian.hobbyadventure.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "에러 응답")
@Getter
public class ErrorResponse {

    @Schema(description = "에러 메시지")
    private final String message;

    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
