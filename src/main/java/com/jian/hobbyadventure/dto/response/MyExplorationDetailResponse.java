package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyExplorationDetailResponse {

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
    private final Long recordId;

    public static MyExplorationDetailResponse from(UserExploration ue, Exploration e, String categoryName, String imageBaseUrl) {
        String thumbnailUrl = e.getThumbnailUrl() != null ? imageBaseUrl + e.getThumbnailUrl() : null;
        Boolean hasRecord = ue.getStatus() == ExplorationStatus.COMPLETED ? false : null;
        return new MyExplorationDetailResponse(
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
                hasRecord,
                null
        );
    }
}
