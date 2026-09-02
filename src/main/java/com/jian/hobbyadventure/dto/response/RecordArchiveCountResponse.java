package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecordArchiveCountResponse {

    private final String month;
    private final long count;

    public static RecordArchiveCountResponse from(String month, long count) {
        return new RecordArchiveCountResponse(month, count);
    }
}
