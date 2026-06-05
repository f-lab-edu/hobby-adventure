package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Record extends BaseEntity {

    private Long id;
    private Long userExplorationId;
    private String title;
    private LocalDate visitedDate;
    private int rating;
    private Emotion emotionCode;
    private String placeName;
    private String content;
    private LocalDateTime updatedAt;

    public static Record create(Long userExplorationId, String title, LocalDate visitedDate,
                                int rating, Emotion emotionCode, String placeName, String content) {
        Record record = new Record();
        record.setUserExplorationId(userExplorationId);
        record.setTitle(title);
        record.setVisitedDate(visitedDate);
        record.setRating(rating);
        record.setEmotionCode(emotionCode);
        record.setPlaceName(placeName);
        record.setContent(content);
        return record;
    }
}
