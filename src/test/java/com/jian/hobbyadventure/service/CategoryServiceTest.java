package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.dto.response.CategoryResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategories_카테고리_목록을_반환한다() {
        List<Category> categories = List.of(
                new Category(1L, "EXERCISE", "운동", 1),
                new Category(2L, "VISIT", "방문", 2)
        );
        when(categoryMapper.findAll()).thenReturn(categories);

        List<CategoryResponse> result = categoryService.getCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("EXERCISE");
        assertThat(result.get(0).getName()).isEqualTo("운동");
    }

    @Test
    void getCategories_데이터가_없으면_빈_리스트를_반환한다() {
        when(categoryMapper.findAll()).thenReturn(List.of());

        List<CategoryResponse> result = categoryService.getCategories();

        assertThat(result).isEmpty();
    }
}
