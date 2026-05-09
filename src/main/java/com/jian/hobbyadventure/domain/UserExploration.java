package com.jian.hobbyadventure.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserExploration {

    private Long id;
    private Long userId;
    private Long explorationId;
    private ExplorationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static UserExploration create(Long userId, Long explorationId) {
        UserExploration ue = new UserExploration();
        ue.userId = userId;
        ue.explorationId = explorationId;
        ue.status = ExplorationStatus.STARTED;
        return ue;
    }
}
