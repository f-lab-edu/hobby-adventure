package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.service.MyExplorationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/my-explorations")
@RequiredArgsConstructor
public class MyExplorationController {

    private final MyExplorationService myExplorationService;

    @GetMapping
    public ResponseEntity<PageResponse<MyExplorationListItemResponse>> getMyExplorations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam ExplorationStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(myExplorationService.getMyExplorations(userId, status, categoryId, page, size));
    }
}
