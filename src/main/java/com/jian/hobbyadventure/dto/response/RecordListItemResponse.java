package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.UserExploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecordListItemResponse {

    private final Long recordId;
    private final Long userExplorationId;
    private final Long explorationId;
    private final String explorationTitle;
    private final Long categoryId;
    private final String categoryName;
    private final String title;
    private final String thumbnailUrl;
    private final LocalDate visitedDate;
    private final int rating;
    private final String emotionCode;
    private final String emotionLabel;
    private final LocalDateTime createdAt;

    public static RecordListItemResponse from(Record record, UserExploration ue, Exploration exploration,
                                              String categoryName, String thumbnailUrl) {
        return new RecordListItemResponse(
                record.getId(),
                ue.getId(),
                exploration.getId(),
                exploration.getTitle(),
                exploration.getCategoryId(),
                categoryName,
                record.getTitle(),
                thumbnailUrl,
                record.getVisitedDate(),
                record.getRating(),
                record.getEmotionCode().name(),
                record.getEmotionCode().getLabel(),
                record.getCreatedAt()
        );
    }
}
