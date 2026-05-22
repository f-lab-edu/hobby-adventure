package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.service.MyExplorationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyExploration", description = "내 탐험 API")
@RestController
@RequestMapping("/api/v1/my-explorations")
@RequiredArgsConstructor
public class MyExplorationController {

    private final MyExplorationService myExplorationService;

    @Operation(summary = "내 탐험 목록 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공 (데이터 없을 경우 빈 배열 반환)")
    @GetMapping
    public ResponseEntity<PageResponse<MyExplorationListItemResponse>> getMyExplorations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam ExplorationStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(myExplorationService.getMyExplorations(userId, status, categoryId, page, size));
    }

    @Operation(summary = "내 탐험 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MyExplorationDetailResponse>> getMyExploration(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.of(myExplorationService.getMyExploration(userId, id)));
    }

    @Operation(summary = "탐험 완료 처리")
    @ApiResponse(responseCode = "200", description = "완료 처리 성공")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<CommonResponse<CompleteExplorationResponse>> completeExploration(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.of(myExplorationService.completeExploration(userId, id)));
    }
}
