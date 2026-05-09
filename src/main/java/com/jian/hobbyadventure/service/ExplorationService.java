package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.dto.response.ExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.ExplorationListItemResponse;
import com.jian.hobbyadventure.dto.response.PageMeta;
import com.jian.hobbyadventure.dto.response.PageResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorationService {

    private final ExplorationMapper explorationMapper;
    private final CategoryMapper categoryMapper;

    @Value("${app.image.base-url}")
    private String imageBaseUrl;

    public PageResponse<ExplorationListItemResponse> getExplorations(Long categoryId, int page, int size) {
        int offset = (page - 1) * size;

        Map<Long, String> categoryNameMap = categoryMapper.findAll()
                .stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName));

        List<Exploration> explorations;
        long totalElements;

        if (categoryId == null) {
            explorations = explorationMapper.findAll(size, offset);
            totalElements = explorationMapper.countAll();
        } else {
            if (!categoryNameMap.containsKey(categoryId)) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
            explorations = explorationMapper.findByCategoryId(categoryId, size, offset);
            totalElements = explorationMapper.countByCategoryId(categoryId);
        }

        List<ExplorationListItemResponse> data = explorations.stream()
                .map(e -> ExplorationListItemResponse.from(e, categoryNameMap.get(e.getCategoryId()), imageBaseUrl))
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }

    public ExplorationDetailResponse getExploration(Long id) {
        Exploration exploration = explorationMapper.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        Category category = categoryMapper.findById(exploration.getCategoryId());
        return ExplorationDetailResponse.from(exploration, category.getName(), imageBaseUrl);
    }
}
