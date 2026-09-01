package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Waypoint extends BaseEntity {

    private Long id;
    private Long userExplorationId;
    private String memo;
    private String placeName;
    private LocalDateTime checkedAt;
    private LocalDateTime updatedAt;

    public static Waypoint create(Long userExplorationId, String memo, String placeName, LocalDateTime checkedAt) {
        Waypoint waypoint = new Waypoint();
        waypoint.setUserExplorationId(userExplorationId);
        waypoint.setMemo(memo);
        waypoint.setPlaceName(placeName);
        waypoint.setCheckedAt(checkedAt);
        return waypoint;
    }
}
