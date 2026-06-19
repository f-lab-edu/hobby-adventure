package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyExplorationService {

    private final UserExplorationMapper userExplorationMapper;
    private final ExplorationMapper explorationMapper;
    private final CategoryMapper categoryMapper;
    private final RecordMapper recordMapper;

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

        List<Long> ueIds = userExplorations.stream().map(UserExploration::getId).toList();
        Set<Long> hasRecordSet = ueIds.isEmpty() ? Set.of() :
                new HashSet<>(recordMapper.findUserExplorationIdsByUserExplorationIdIn(ueIds));

        List<MyExplorationListItemResponse> data = userExplorations.stream()
                .map(ue -> {
                    Exploration e = explorationMap.get(ue.getExplorationId());
                    String categoryName = categoryNameMap.get(e.getCategoryId());
                    Boolean hasRecord = toHasRecord(ue.getStatus(), hasRecordSet.contains(ue.getId()));
                    return MyExplorationListItemResponse.from(ue, e, categoryName, imageBaseUrl, hasRecord);
                })
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }

    public MyExplorationDetailResponse getMyExploration(Long userId, Long userExplorationId) {
        UserExploration userExploration = userExplorationMapper.findById(userExplorationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Exploration exploration = explorationMapper.findById(userExploration.getExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Category category = categoryMapper.findById(exploration.getCategoryId());

        Record record = recordMapper.findByUserExplorationId(userExplorationId).orElse(null);
        Boolean hasRecord = toHasRecord(userExploration.getStatus(), record != null);
        Long recordId = record != null ? record.getId() : null;

        return MyExplorationDetailResponse.from(userExploration, exploration, category.getName(), imageBaseUrl, hasRecord, recordId);
    }

    private Boolean toHasRecord(ExplorationStatus status, boolean recordExists) {
        return status == ExplorationStatus.COMPLETED ? recordExists : null;
    }

    public CompleteExplorationResponse completeExploration(Long userId, Long userExplorationId) {
        UserExploration userExploration = userExplorationMapper.findById(userExplorationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (userExploration.getStatus() == ExplorationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_STATE);
        }

        userExplorationMapper.complete(userExplorationId);

        return new CompleteExplorationResponse(userExplorationId);
    }
}
