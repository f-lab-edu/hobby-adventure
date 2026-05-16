package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExplorationDetailResponse {

    private final Long id;
    private final String categoryName;
    private final String title;
    private final String thumbnailUrl;
    private final String shortDescription;
    private final String description;
    private final LocalDateTime createdAt;

    public static ExplorationDetailResponse from(Exploration exploration, String categoryName, String imageBaseUrl) {
        String thumbnailUrl = exploration.getThumbnailUrl() != null
                ? imageBaseUrl + exploration.getThumbnailUrl()
                : null;
        return new ExplorationDetailResponse(
                exploration.getId(),
                categoryName,
                exploration.getTitle(),
                thumbnailUrl,
                exploration.getShortDescription(),
                exploration.getDescription(),
                exploration.getCreatedAt()
        );
    }
}
