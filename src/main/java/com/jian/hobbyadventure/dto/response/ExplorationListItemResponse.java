package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExplorationListItemResponse {

    private final Long id;
    private final String categoryName;
    private final String title;
    private final String thumbnailUrl;
    private final String shortDescription;

    public static ExplorationListItemResponse from(Exploration exploration, String categoryName, String imageBaseUrl) {
        String thumbnailUrl = exploration.getThumbnailUrl() != null
                ? imageBaseUrl + exploration.getThumbnailUrl()
                : null;
        return new ExplorationListItemResponse(
                exploration.getId(),
                categoryName,
                exploration.getTitle(),
                thumbnailUrl,
                exploration.getShortDescription()
        );
    }
}
