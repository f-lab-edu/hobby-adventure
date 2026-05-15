package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Exploration {

    private Long id;
    private Long categoryId;
    private String title;
    private String thumbnailUrl;
    private String shortDescription;
    private String description;
    private LocalDateTime createdAt;
}
