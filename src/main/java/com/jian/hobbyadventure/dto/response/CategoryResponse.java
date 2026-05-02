package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long categoryId;
    private String code;
    private String name;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCode(),
                category.getName()
        );
    }
}
