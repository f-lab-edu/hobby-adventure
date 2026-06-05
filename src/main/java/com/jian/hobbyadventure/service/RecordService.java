package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.RecordImage;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.dto.response.CreateRecordResponse;
import com.jian.hobbyadventure.dto.response.DeleteRecordResponse;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.RecordListItemResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordImageMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordMapper recordMapper;
    private final RecordImageMapper recordImageMapper;
    private final UserExplorationMapper userExplorationMapper;
    private final ExplorationMapper explorationMapper;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    @Value("${app.image.base-url}")
    private String imageBaseUrl;

    @Transactional
    public CreateRecordResponse createRecord(Long userId, CreateRecordRequest request, List<MultipartFile> images) {
        UserExploration userExploration = userExplorationMapper.findById(request.getUserExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (userExploration.getStatus() != ExplorationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_STATE);
        }

        if (recordMapper.existsByUserExplorationId(request.getUserExplorationId())) {
            throw new BusinessException(ErrorCode.DUPLICATED);
        }

        Record record = Record.create(
                request.getUserExplorationId(),
                request.getTitle(),
                request.getVisitedDate(),
                request.getRating(),
                request.getEmotionCode(),
                request.getPlaceName(),
                request.getContent()
        );

        recordMapper.insert(record);

        if (images != null && !images.isEmpty()) {
            saveRecordImages(record.getId(), images);
        }

        return new CreateRecordResponse(record.getId());
    }

    public PageResponse<RecordListItemResponse> getRecords(Long userId, Long categoryId, Long explorationId, int page, int size) {
        int offset = (page - 1) * size;

        List<Long> userExplorationIds;
        if (explorationId != null) {
            userExplorationIds = userExplorationMapper.findIdsByUserIdAndExplorationIds(userId, List.of(explorationId));
        } else if (categoryId != null) {
            List<Long> explorationIds = explorationMapper.findIdsByCategoryId(categoryId);
            userExplorationIds = explorationIds.isEmpty()
                    ? List.of()
                    : userExplorationMapper.findIdsByUserIdAndExplorationIds(userId, explorationIds);
        } else {
            userExplorationIds = userExplorationMapper.findIdsByUserId(userId);
        }

        if (userExplorationIds.isEmpty()) {
            return PageResponse.of(List.of(), PageMeta.of(page, size, 0));
        }

        List<Record> records = recordMapper.findAllByUserExplorationIds(userExplorationIds, size, offset);
        long totalElements = recordMapper.countByUserExplorationIds(userExplorationIds);

        if (records.isEmpty()) {
            return PageResponse.of(List.of(), PageMeta.of(page, size, totalElements));
        }

        List<Long> recordIds = records.stream().map(Record::getId).toList();
        Map<Long, String> thumbnailMap = buildThumbnailMap(recordIds);

        List<Long> ueIds = records.stream().map(Record::getUserExplorationId).distinct().toList();
        Map<Long, UserExploration> ueMap = userExplorationMapper.findByIdIn(ueIds).stream()
                .collect(Collectors.toMap(UserExploration::getId, ue -> ue));

        List<Long> explorationIds = ueMap.values().stream().map(UserExploration::getExplorationId).distinct().toList();
        Map<Long, Exploration> explorationMap = explorationMapper.findByIdIn(explorationIds).stream()
                .collect(Collectors.toMap(Exploration::getId, e -> e));

        Map<Long, String> categoryNameMap = categoryMapper.findAll().stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName));

        List<RecordListItemResponse> data = records.stream()
                .map(record -> {
                    UserExploration ue = ueMap.get(record.getUserExplorationId());
                    Exploration exploration = explorationMap.get(ue.getExplorationId());
                    String categoryName = categoryNameMap.get(exploration.getCategoryId());
                    String thumbUrl = thumbnailMap.getOrDefault(record.getId(),
                            exploration.getThumbnailUrl() != null ? imageBaseUrl + exploration.getThumbnailUrl() : null);
                    return RecordListItemResponse.from(record, ue, exploration, categoryName, thumbUrl);
                })
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }

    public RecordDetailResponse getRecord(Long userId, Long recordId) {
        Record record = recordMapper.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        UserExploration userExploration = userExplorationMapper.findById(record.getUserExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Exploration exploration = explorationMapper.findById(userExploration.getExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Category category = categoryMapper.findById(exploration.getCategoryId());

        List<String> imageUrls = recordImageMapper.findAllByRecordId(recordId).stream()
                .map(img -> imageBaseUrl + img.getImageUrl())
                .toList();

        return RecordDetailResponse.from(record, userExploration, exploration, category.getName(), imageUrls);
    }

    @Transactional
    public UpdateRecordResponse updateRecord(Long userId, Long recordId, UpdateRecordRequest request, List<MultipartFile> newImages) {
        Record record = recordMapper.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        UserExploration userExploration = userExplorationMapper.findById(record.getUserExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (request.getTitle() != null) record.setTitle(request.getTitle());
        if (request.getVisitedDate() != null) record.setVisitedDate(request.getVisitedDate());
        if (request.getRating() != null) record.setRating(request.getRating());
        if (request.getEmotionCode() != null) record.setEmotionCode(request.getEmotionCode());
        if (request.getContent() != null) record.setContent(request.getContent());
        // placeName: 빈 문자열로 전달하면 null(삭제), 값이 있으면 업데이트, null이면 변경 없음
        if (request.getPlaceName() != null) {
            record.setPlaceName(request.getPlaceName().isBlank() ? null : request.getPlaceName());
        }

        recordMapper.update(record);

        if (newImages != null) {
            imageService.deleteImages(recordId);
            recordImageMapper.deleteAllByRecordId(recordId);
            if (!newImages.isEmpty()) {
                saveRecordImages(recordId, newImages);
            }
        }

        return new UpdateRecordResponse(recordId);
    }

    @Transactional
    public DeleteRecordResponse deleteRecord(Long userId, Long recordId) {
        Record record = recordMapper.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        UserExploration userExploration = userExplorationMapper.findById(record.getUserExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        imageService.deleteImages(recordId);
        recordImageMapper.deleteAllByRecordId(recordId);
        recordMapper.deleteById(recordId);

        return new DeleteRecordResponse(recordId);
    }

    private void saveRecordImages(Long recordId, List<MultipartFile> files) {
        List<String> relativePaths = imageService.saveImages(recordId, files);
        List<RecordImage> recordImages = new ArrayList<>();
        for (int i = 0; i < relativePaths.size(); i++) {
            RecordImage ri = new RecordImage();
            ri.setRecordId(recordId);
            ri.setImageUrl(relativePaths.get(i));
            ri.setImageOrder(i + 1);
            recordImages.add(ri);
        }
        recordImageMapper.insertAll(recordImages);
    }

    private Map<Long, String> buildThumbnailMap(List<Long> recordIds) {
        return recordImageMapper.findAllByRecordIds(recordIds).stream()
                .collect(Collectors.toMap(
                        RecordImage::getRecordId,
                        img -> imageBaseUrl + img.getImageUrl(),
                        (existing, replacement) -> existing
                ));
    }
}
