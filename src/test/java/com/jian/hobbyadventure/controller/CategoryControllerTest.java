package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.dto.response.CategoryResponse;
import com.jian.hobbyadventure.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getCategories_성공_시_200을_반환한다() throws Exception {
        List<CategoryResponse> categories = List.of(
                new CategoryResponse(1L, "EXERCISE", "운동"),
                new CategoryResponse(2L, "VISIT", "방문")
        );
        when(categoryService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("EXERCISE"))
                .andExpect(jsonPath("$.data[0].name").value("운동"));
    }

    @Test
    void getCategories_데이터가_없을_때_빈_배열을_반환한다() throws Exception {
        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
