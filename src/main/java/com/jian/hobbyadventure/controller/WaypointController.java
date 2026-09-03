package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.request.CreateWaypointRequest;
import com.jian.hobbyadventure.dto.request.UpdateWaypointRequest;
import com.jian.hobbyadventure.dto.response.CreateWaypointResponse;
import com.jian.hobbyadventure.dto.response.DeleteWaypointResponse;
import com.jian.hobbyadventure.dto.response.UpdateWaypointResponse;
import com.jian.hobbyadventure.dto.response.WaypointDetailResponse;
import com.jian.hobbyadventure.dto.response.WaypointListItemResponse;
import com.jian.hobbyadventure.service.WaypointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Waypoint", description = "여정 API")
@RestController
@RequestMapping("/api/v1/waypoints")
@RequiredArgsConstructor
public class WaypointController {

    private final WaypointService waypointService;

    @Operation(summary = "여정 목록 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공 (데이터 없을 경우 빈 배열 반환)")
    @GetMapping
    public ResponseEntity<PageResponse<WaypointListItemResponse>> getWaypoints(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long userExplorationId,
            @RequestParam(defaultValue = "newest") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(waypointService.getWaypoints(userId, userExplorationId, sortOrder, page, size));
    }

    @Operation(summary = "여정 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{waypointId}")
    public ResponseEntity<CommonResponse<WaypointDetailResponse>> getWaypoint(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long waypointId) {
        return ResponseEntity.ok(CommonResponse.of(waypointService.getWaypoint(userId, waypointId)));
    }

    @Operation(summary = "여정 생성")
    @ApiResponse(responseCode = "201", description = "여정 생성 성공")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<CreateWaypointResponse>> createWaypoint(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("request") @Valid CreateWaypointRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(201).body(CommonResponse.of(waypointService.createWaypoint(userId, request, images)));
    }

    @Operation(summary = "여정 수정")
    @ApiResponse(responseCode = "200", description = "여정 수정 성공")
    @PatchMapping(value = "/{waypointId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<UpdateWaypointResponse>> updateWaypoint(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long waypointId,
            @RequestPart("request") @Valid UpdateWaypointRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(CommonResponse.of(waypointService.updateWaypoint(userId, waypointId, request, images)));
    }

    @Operation(summary = "여정 삭제")
    @ApiResponse(responseCode = "200", description = "여정 삭제 성공")
    @DeleteMapping("/{waypointId}")
    public ResponseEntity<CommonResponse<DeleteWaypointResponse>> deleteWaypoint(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long waypointId) {
        return ResponseEntity.ok(CommonResponse.of(waypointService.deleteWaypoint(userId, waypointId)));
    }
}
