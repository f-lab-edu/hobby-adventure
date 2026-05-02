package com.jian.hobbyadventure.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long categoryId;
    private String code;
    private String name;
    private int displayOrder;
}
