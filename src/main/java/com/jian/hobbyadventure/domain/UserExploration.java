package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserExploration {

    private Long id;
    private Long userId;
    private Long explorationId;
    private ExplorationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static UserExploration create(Long userId, Long explorationId) {
        UserExploration ue = new UserExploration();
        ue.setUserId(userId);
        ue.setExplorationId(explorationId);
        ue.setStatus(ExplorationStatus.STARTED);
        return ue;
    }
}
