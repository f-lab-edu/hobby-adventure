package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.common.response.ErrorResponse;
import com.jian.hobbyadventure.dto.response.ExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.ExplorationListItemResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.response.StartExplorationResponse;
import com.jian.hobbyadventure.service.ExplorationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exploration", description = "탐험 API")
@RestController
@RequestMapping("/api/v1/explorations")
@RequiredArgsConstructor
public class ExplorationController {

    private final ExplorationService explorationService;

    @Operation(summary = "탐험 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탐험 목록 조회 성공 (데이터 없을 경우 빈 배열 반환)"),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)), description = "존재하지 않는 카테고리")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ExplorationListItemResponse>> getExplorations(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(explorationService.getExplorations(categoryId, page, size));
    }

    @Operation(summary = "탐험 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탐험 단건 조회 성공"),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)), description = "존재하지 않는 탐험")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExplorationDetailResponse>> getExploration(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.of(explorationService.getExploration(id)));
    }

    @Operation(summary = "탐험 시작")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탐험 시작 성공"),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)), description = "존재하지 않는 탐험 또는 사용자")
    })
    @PostMapping("/{id}/start")
    public ResponseEntity<CommonResponse<StartExplorationResponse>> startExploration(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(CommonResponse.of(explorationService.startExploration(id, userId)));
    }
}
