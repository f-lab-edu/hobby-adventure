package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserExploration extends BaseEntity {

    private Long id;
    private Long userId;
    private Long explorationId;
    private ExplorationStatus status;
    private LocalDateTime completedAt;

    public static UserExploration create(Long userId, Long explorationId) {
        UserExploration ue = new UserExploration();
        ue.setUserId(userId);
        ue.setExplorationId(explorationId);
        ue.setStatus(ExplorationStatus.STARTED);
        return ue;
    }

    // TODO: 기록 기능 미구현으로 false 고정. 기록 기능 추가 시 실제 기록 보유 여부를 반환하도록 변경
    public Boolean hasRecord() {
        if (this.status != ExplorationStatus.COMPLETED) return null;
        return false;
    }
}
