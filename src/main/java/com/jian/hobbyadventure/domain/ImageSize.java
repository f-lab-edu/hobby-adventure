package com.jian.hobbyadventure.domain;

public enum ImageSize {

    LIST("list"),
    DETAIL("detail");

    private final String code;

    ImageSize(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
