package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyExplorationService {

    private final UserExplorationMapper userExplorationMapper;
    private final ExplorationMapper explorationMapper;
    private final CategoryMapper categoryMapper;

    @Value("${app.image.base-url}")
    private String imageBaseUrl;

    public PageResponse<MyExplorationListItemResponse> getMyExplorations(Long userId, ExplorationStatus status, Long categoryId, int page, int size) {
        int offset = (page - 1) * size;

        List<Long> explorationIds = null;
        if (categoryId != null) {
            explorationIds = explorationMapper.findIdsByCategoryId(categoryId);
        }

        List<UserExploration> userExplorations = userExplorationMapper.findAllByCondition(userId, status, explorationIds, size, offset);
        long totalElements = userExplorationMapper.countByCondition(userId, status, explorationIds);

        List<Long> ids = userExplorations.stream().map(UserExploration::getExplorationId).toList();
        Map<Long, Exploration> explorationMap = ids.isEmpty() ? Map.of() :
                explorationMapper.findByIdIn(ids).stream().collect(Collectors.toMap(Exploration::getId, e -> e));

        Map<Long, String> categoryNameMap = categoryMapper.findAll().stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName));

        List<MyExplorationListItemResponse> data = userExplorations.stream()
                .map(ue -> {
                    Exploration e = explorationMap.get(ue.getExplorationId());
                    String categoryName = categoryNameMap.get(e.getCategoryId());
                    return MyExplorationListItemResponse.from(ue, e, categoryName, imageBaseUrl);
                })
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }
}
