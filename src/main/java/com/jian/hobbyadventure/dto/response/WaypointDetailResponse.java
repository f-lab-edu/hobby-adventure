package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Waypoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class WaypointDetailResponse {

    private final Long waypointId;
    private final Long userExplorationId;
    private final String memo;
    private final String placeName;
    private final LocalDateTime checkedAt;
    private final List<WaypointImageResponse> images;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static WaypointDetailResponse from(Waypoint waypoint, List<WaypointImageResponse> images) {
        return new WaypointDetailResponse(
                waypoint.getId(),
                waypoint.getUserExplorationId(),
                waypoint.getMemo(),
                waypoint.getPlaceName(),
                waypoint.getCheckedAt(),
                images,
                waypoint.getCreatedAt(),
                waypoint.getUpdatedAt()
        );
    }
}
