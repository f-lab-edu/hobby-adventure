package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.ImageSize;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.domain.Waypoint;
import com.jian.hobbyadventure.domain.WaypointImage;
import com.jian.hobbyadventure.dto.request.CreateWaypointRequest;
import com.jian.hobbyadventure.dto.request.UpdateWaypointRequest;
import com.jian.hobbyadventure.dto.response.CreateWaypointResponse;
import com.jian.hobbyadventure.dto.response.DeleteWaypointResponse;
import com.jian.hobbyadventure.dto.response.UpdateWaypointResponse;
import com.jian.hobbyadventure.dto.response.WaypointDetailResponse;
import com.jian.hobbyadventure.dto.response.WaypointImageResponse;
import com.jian.hobbyadventure.dto.response.WaypointListItemResponse;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import com.jian.hobbyadventure.repository.WaypointImageMapper;
import com.jian.hobbyadventure.repository.WaypointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WaypointService {

    private static final int MAX_IMAGE_COUNT = 4;

    private final WaypointMapper waypointMapper;
    private final WaypointImageMapper waypointImageMapper;
    private final UserExplorationMapper userExplorationMapper;
    private final ImageService imageService;

    @Transactional
    public CreateWaypointResponse createWaypoint(Long userId, CreateWaypointRequest request, List<MultipartFile> images) {
        if (images != null && images.size() > MAX_IMAGE_COUNT) {
            throw new BusinessException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        UserExploration userExploration = validateOwnerAndStarted(userId, request.getUserExplorationId());

        Waypoint waypoint = Waypoint.create(
                userExploration.getId(),
                request.getMemo(),
                request.getPlaceName(),
                request.getCheckedAt()
        );

        waypointMapper.insert(waypoint);

        if (images != null && !images.isEmpty()) {
            saveWaypointImages(waypoint.getId(), images, 1);
        }

        return new CreateWaypointResponse(waypoint.getId());
    }

    public PageResponse<WaypointListItemResponse> getWaypoints(Long userId, Long userExplorationId, String sortOrder, int page, int size) {
        UserExploration userExploration = userExplorationMapper.findById(userExplorationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        int offset = (page - 1) * size;
        List<Waypoint> waypoints = waypointMapper.findAllByUserExplorationId(userExplorationId, sortOrder, size, offset);
        long totalElements = waypointMapper.countByUserExplorationId(userExplorationId);

        if (waypoints.isEmpty()) {
            return PageResponse.of(List.of(), PageMeta.of(page, size, totalElements));
        }

        List<Long> waypointIds = waypoints.stream().map(Waypoint::getId).toList();
        Map<Long, List<WaypointImage>> imagesByWaypointId = waypointImageMapper.findAllByWaypointIds(waypointIds).stream()
                .collect(Collectors.groupingBy(WaypointImage::getWaypointId));

        List<WaypointListItemResponse> data = waypoints.stream()
                .map(waypoint -> {
                    List<WaypointImage> waypointImages = imagesByWaypointId.getOrDefault(waypoint.getId(), List.of());
                    String thumbnailUrl = waypointImages.isEmpty()
                            ? null
                            : imageService.generateSignedCloudFrontUrl(waypointImages.get(0).getImageUrl(), ImageSize.LIST);
                    return WaypointListItemResponse.from(waypoint, thumbnailUrl, waypointImages.size());
                })
                .toList();

        return PageResponse.of(data, PageMeta.of(page, size, totalElements));
    }

    public WaypointDetailResponse getWaypoint(Long userId, Long waypointId) {
        Waypoint waypoint = waypointMapper.findById(waypointId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        UserExploration userExploration = userExplorationMapper.findById(waypoint.getUserExplorationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        List<WaypointImageResponse> images = waypointImageMapper.findAllByWaypointId(waypointId).stream()
                .map(img -> new WaypointImageResponse(img.getId(), imageService.generateSignedCloudFrontUrl(img.getImageUrl(), ImageSize.DETAIL)))
                .toList();

        return WaypointDetailResponse.from(waypoint, images);
    }

    @Transactional
    public UpdateWaypointResponse updateWaypoint(Long userId, Long waypointId, UpdateWaypointRequest request, List<MultipartFile> newImages) {
        Waypoint waypoint = waypointMapper.findById(waypointId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        validateOwnerAndStarted(userId, waypoint.getUserExplorationId());

        List<WaypointImage> currentImages = waypointImageMapper.findAllByWaypointId(waypointId);
        List<Long> deleteImageIds = request.getDeleteImageIds() != null ? request.getDeleteImageIds() : List.of();
        int newImageCount = newImages != null ? newImages.size() : 0;

        Map<Long, WaypointImage> currentImageMap = currentImages.stream()
                .collect(Collectors.toMap(WaypointImage::getId, img -> img));
        for (Long imageId : deleteImageIds) {
            if (!currentImageMap.containsKey(imageId)) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
        }

        int resultingImageCount = currentImages.size() - deleteImageIds.size() + newImageCount;
        if (resultingImageCount > MAX_IMAGE_COUNT) {
            throw new BusinessException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        if (request.getMemo() != null) waypoint.setMemo(request.getMemo());
        if (request.getCheckedAt() != null) waypoint.setCheckedAt(request.getCheckedAt());
        // placeName: 빈 문자열로 전달하면 null(삭제), 값이 있으면 업데이트, null이면 변경 없음
        if (request.getPlaceName() != null) {
            waypoint.setPlaceName(request.getPlaceName().isBlank() ? null : request.getPlaceName());
        }

        waypointMapper.update(waypoint);

        if (!deleteImageIds.isEmpty()) {
            List<String> deletedKeys = deleteImageIds.stream()
                    .map(id -> currentImageMap.get(id).getImageUrl())
                    .toList();
            deleteImageIds.forEach(waypointImageMapper::deleteById);
            imageService.deleteImages(deletedKeys);
        }

        if (newImageCount > 0) {
            int maxRemainingOrder = currentImages.stream()
                    .filter(img -> !deleteImageIds.contains(img.getId()))
                    .mapToInt(WaypointImage::getImageOrder)
                    .max()
                    .orElse(0);
            saveWaypointImages(waypointId, newImages, maxRemainingOrder + 1);
        }

        return new UpdateWaypointResponse(waypointId);
    }

    @Transactional
    public DeleteWaypointResponse deleteWaypoint(Long userId, Long waypointId) {
        Waypoint waypoint = waypointMapper.findById(waypointId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        validateOwnerAndStarted(userId, waypoint.getUserExplorationId());

        List<String> imageKeys = waypointImageMapper.findAllByWaypointId(waypointId).stream()
                .map(WaypointImage::getImageUrl)
                .toList();

        waypointImageMapper.deleteAllByWaypointId(waypointId);
        waypointMapper.deleteById(waypointId);
        imageService.deleteImages(imageKeys);

        return new DeleteWaypointResponse(waypointId);
    }

    private UserExploration validateOwnerAndStarted(Long userId, Long userExplorationId) {
        UserExploration userExploration = userExplorationMapper.findById(userExplorationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!userExploration.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (userExploration.getStatus() != ExplorationStatus.STARTED) {
            throw new BusinessException(ErrorCode.INVALID_STATE);
        }

        return userExploration;
    }

    private void saveWaypointImages(Long waypointId, List<MultipartFile> files, int startOrder) {
        List<String> relativePaths = imageService.saveWaypointImages(waypointId, files);
        List<WaypointImage> waypointImages = new ArrayList<>();
        for (int i = 0; i < relativePaths.size(); i++) {
            WaypointImage wi = new WaypointImage();
            wi.setWaypointId(waypointId);
            wi.setImageUrl(relativePaths.get(i));
            wi.setImageOrder(startOrder + i);
            waypointImages.add(wi);
        }
        waypointImageMapper.insertAll(waypointImages);
    }
}
