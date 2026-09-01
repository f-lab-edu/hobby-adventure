package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Waypoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaypointListItemResponse {

    private final Long waypointId;
    private final Long userExplorationId;
    private final String memo;
    private final String placeName;
    private final LocalDateTime checkedAt;
    private final String thumbnailUrl;
    private final int photoCount;
    private final LocalDateTime createdAt;

    public static WaypointListItemResponse from(Waypoint waypoint, String thumbnailUrl, int photoCount) {
        return new WaypointListItemResponse(
                waypoint.getId(),
                waypoint.getUserExplorationId(),
                waypoint.getMemo(),
                waypoint.getPlaceName(),
                waypoint.getCheckedAt(),
                thumbnailUrl,
                photoCount,
                waypoint.getCreatedAt()
        );
    }
}
