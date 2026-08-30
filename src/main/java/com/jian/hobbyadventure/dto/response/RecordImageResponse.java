package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecordImageResponse {

    private final Long imageId;
    private final String url;
}
