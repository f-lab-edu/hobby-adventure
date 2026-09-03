package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExplorationCountResponse {

    private final Long explorationId;
    private final String title;
    private final long count;

    public static ExplorationCountResponse from(Long explorationId, String title, long count) {
        return new ExplorationCountResponse(explorationId, title, count);
    }
}
