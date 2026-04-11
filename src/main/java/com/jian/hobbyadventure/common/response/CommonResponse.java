package com.jian.hobbyadventure.common.response;

import lombok.Getter;

@Getter
public class CommonResponse<T> {

    private final T data;

    private CommonResponse(T data) {
        this.data = data;
    }

    public static <T> CommonResponse<T> of(T data) {
        return new CommonResponse<>(data);
    }
}
