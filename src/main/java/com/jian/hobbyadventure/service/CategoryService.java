package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.dto.response.CategoryResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategories() {
        return categoryMapper.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
