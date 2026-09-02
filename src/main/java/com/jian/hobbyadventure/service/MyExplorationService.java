package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.ImageSize;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.ExplorationCountResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationCountRow;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final ImageService imageService;

    public PageResponse<MyExplorationListItemResponse> getMyExplorations(Long userId, ExplorationStatus status, Long categoryId, Long explorationId, Boolean hasRecord, int page, int size) {
        int offset = (page - 1) * size;

        List<Long> explorationIds = null;
        if (explorationId != null) {
            // 특정 탐험 하나만 지정된 경우 — 카테고리보다 더 좁은 조건이라 그대로 우선
            explorationIds = List.of(explorationId);
        } else if (categoryId != null) {
            explorationIds = explorationMapper.findIdsByCategoryId(categoryId);
        }

        // hasRecord는 COMPLETED에만 의미가 있음 — 기록 유무로 먼저 userExploration id를 걸러서 그 안에서만 조회
        List<Long> userExplorationIds = null;
        if (status == ExplorationStatus.COMPLETED && hasRecord != null) {
            userExplorationIds = filterIdsByHasRecord(userId, hasRecord);
            if (userExplorationIds.isEmpty()) {
                return PageResponse.of(List.of(), PageMeta.of(page, size, 0));
            }
        }

        List<UserExploration> userExplorations = userExplorationMapper.findAllByCondition(userId, status, explorationIds, userExplorationIds, size, offset);
        long totalElements = userExplorationMapper.countByCondition(userId, status, explorationIds, userExplorationIds);

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
                    Boolean rowHasRecord = toHasRecord(ue.getStatus(), hasRecordSet.contains(ue.getId()));
                    return MyExplorationListItemResponse.from(ue, e, categoryName, resolveThumbnailUrl(e, ImageSize.LIST), rowHasRecord);
                })
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }

    // JOIN 없이: 완료한 전체 id를 먼저 뽑고, 그중 기록 있는 id를 recordMapper로 따로 조회해서 Java에서 차집합
    private List<Long> filterIdsByHasRecord(Long userId, boolean hasRecord) {
        List<Long> completedIds = userExplorationMapper.findIdsByUserIdAndStatus(userId, ExplorationStatus.COMPLETED);
        if (completedIds.isEmpty()) {
            return List.of();
        }
        Set<Long> recordedIds = new HashSet<>(recordMapper.findUserExplorationIdsByUserExplorationIdIn(completedIds));
        if (hasRecord) {
            return new ArrayList<>(recordedIds);
        }
        return completedIds.stream().filter(id -> !recordedIds.contains(id)).toList();
    }

    // 완료 탐험을 탐험(explorationId) 단위로 묶어서 개수만 반환 — "탐험별 필터" 드롭다운용, user_explorations 한 테이블만 GROUP BY(JOIN 없음)
    public List<ExplorationCountResponse> getCompletedExplorationCounts(Long userId) {
        List<UserExplorationCountRow> rows = userExplorationMapper.countGroupByExplorationId(userId, ExplorationStatus.COMPLETED);
        if (rows.isEmpty()) {
            return List.of();
        }

        List<Long> explorationIds = rows.stream().map(UserExplorationCountRow::getExplorationId).toList();
        Map<Long, String> titleMap = explorationMapper.findByIdIn(explorationIds).stream()
                .collect(Collectors.toMap(Exploration::getId, Exploration::getTitle));

        return rows.stream()
                .map(row -> ExplorationCountResponse.from(row.getExplorationId(), titleMap.get(row.getExplorationId()), row.getCount()))
                .toList();
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

        return MyExplorationDetailResponse.from(userExploration, exploration, category.getName(), resolveThumbnailUrl(exploration, ImageSize.DETAIL), hasRecord, recordId);
    }

    private Boolean toHasRecord(ExplorationStatus status, boolean recordExists) {
        return status == ExplorationStatus.COMPLETED ? recordExists : null;
    }

    private String resolveThumbnailUrl(Exploration exploration, ImageSize size) {
        return exploration.getThumbnailUrl() != null
                ? imageService.generatePublicCloudFrontUrl(exploration.getThumbnailUrl(), size)
                : null;
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
