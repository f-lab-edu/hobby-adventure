package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageMeta {

    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    public static PageMeta of(int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages;
        return new PageMeta(page, size, totalElements, totalPages, hasNext);
    }
}
