package com.jian.hobbyadventure.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Exploration {

    private Long id;
    private Long categoryId;
    private String title;
    private String thumbnailUrl;
    private String shortDescription;
    private String description;
    private LocalDateTime createdAt;
}
