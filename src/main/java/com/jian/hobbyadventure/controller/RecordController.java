package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.dto.response.CreateRecordResponse;
import com.jian.hobbyadventure.dto.response.DeleteRecordResponse;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.RecordListItemResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.service.RecordService;
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

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<PageResponse<RecordListItemResponse>> getRecords(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long explorationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(recordService.getRecords(userId, categoryId, explorationId, page, size));
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<CommonResponse<RecordDetailResponse>> getRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(CommonResponse.of(recordService.getRecord(userId, recordId)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<CreateRecordResponse>> createRecord(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("request") @Valid CreateRecordRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(201).body(CommonResponse.of(recordService.createRecord(userId, request, images)));
    }

    @PatchMapping(value = "/{recordId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<UpdateRecordResponse>> updateRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId,
            @RequestPart("request") @Valid UpdateRecordRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(CommonResponse.of(recordService.updateRecord(userId, recordId, request, images)));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<CommonResponse<DeleteRecordResponse>> deleteRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(CommonResponse.of(recordService.deleteRecord(userId, recordId)));
    }
}
