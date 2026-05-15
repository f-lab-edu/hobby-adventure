package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Exploration extends BaseEntity {

    private Long id;
    private Long categoryId;
    private String title;
    private String thumbnailUrl;
    private String shortDescription;
    private String description;
}
