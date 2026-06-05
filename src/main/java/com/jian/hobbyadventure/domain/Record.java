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
    private Emotion emotion;
    private String placeName;
    private String content;
    private LocalDateTime updatedAt;
}
