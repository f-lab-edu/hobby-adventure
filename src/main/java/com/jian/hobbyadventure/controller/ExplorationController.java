package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.dto.response.ExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.ExplorationListItemResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.response.StartExplorationResponse;
import com.jian.hobbyadventure.service.ExplorationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/explorations")
@RequiredArgsConstructor
public class ExplorationController {

    private final ExplorationService explorationService;

    @GetMapping
    public ResponseEntity<PageResponse<ExplorationListItemResponse>> getExplorations(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(explorationService.getExplorations(categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExplorationDetailResponse>> getExploration(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.of(explorationService.getExploration(id)));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<CommonResponse<StartExplorationResponse>> startExploration(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(CommonResponse.of(explorationService.startExploration(id, userId)));
    }
}
