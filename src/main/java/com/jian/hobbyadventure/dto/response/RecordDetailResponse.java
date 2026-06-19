package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.UserExploration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecordDetailResponse {

    private final Long recordId;
    private final Long userExplorationId;
    private final Long explorationId;
    private final String explorationTitle;
    private final Long categoryId;
    private final String categoryName;
    private final String title;
    private final LocalDate visitedDate;
    private final int rating;
    private final String emotionCode;
    private final String emotionLabel;
    private final String placeName;
    private final String content;
    private final List<String> imageUrls;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static RecordDetailResponse from(Record record, UserExploration ue, Exploration exploration,
                                            String categoryName, List<String> imageUrls) {
        return new RecordDetailResponse(
                record.getId(),
                ue.getId(),
                exploration.getId(),
                exploration.getTitle(),
                exploration.getCategoryId(),
                categoryName,
                record.getTitle(),
                record.getVisitedDate(),
                record.getRating(),
                record.getEmotionCode().name(),
                record.getEmotionCode().getLabel(),
                record.getPlaceName(),
                record.getContent(),
                imageUrls,
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
