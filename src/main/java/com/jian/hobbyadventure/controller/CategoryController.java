package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.dto.response.CategoryResponse;
import com.jian.hobbyadventure.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category", description = "카테고리 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공 (데이터 없을 경우 빈 배열 반환)")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> categories = categoryService.getCategories();
        return ResponseEntity.ok(CommonResponse.of(categories));
    }
}
