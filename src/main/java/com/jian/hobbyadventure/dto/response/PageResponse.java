package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private final List<T> data;
    private final PageMeta meta;

    public static <T> PageResponse<T> of(List<T> data, PageMeta meta) {
        return new PageResponse<>(data, meta);
    }
}
