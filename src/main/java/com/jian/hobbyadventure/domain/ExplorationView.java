package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ExplorationView {

    private Long id;
    private String viewEventId;
    private Long explorationId;
    private LocalDateTime viewedAt;

    public ExplorationView(String viewEventId, Long explorationId, LocalDateTime viewedAt) {
        this.viewEventId = viewEventId;
        this.explorationId = explorationId;
        this.viewedAt = viewedAt;
    }
}
