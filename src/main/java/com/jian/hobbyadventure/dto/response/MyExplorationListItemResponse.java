package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyExplorationListItemResponse {

    private final Long userExplorationId;
    private final Long explorationId;
    private final String title;
    private final String thumbnailUrl;
    private final Long categoryId;
    private final String categoryName;
    private final String shortDescription;
    private final ExplorationStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final Boolean hasRecord;

    public static MyExplorationListItemResponse from(UserExploration ue, Exploration e, String categoryName, String imageBaseUrl, Boolean hasRecord) {
        String thumbnailUrl = e.getThumbnailUrl() != null ? imageBaseUrl + e.getThumbnailUrl() : null;
        return new MyExplorationListItemResponse(
                ue.getId(),
                e.getId(),
                e.getTitle(),
                thumbnailUrl,
                e.getCategoryId(),
                categoryName,
                e.getShortDescription(),
                ue.getStatus(),
                ue.getCreatedAt(),
                ue.getCompletedAt(),
                hasRecord
        );
    }
}
