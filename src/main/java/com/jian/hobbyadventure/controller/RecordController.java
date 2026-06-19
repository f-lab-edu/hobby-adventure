package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.RecordSearchCondition;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.dto.response.CreateRecordResponse;
import com.jian.hobbyadventure.dto.response.DeleteRecordResponse;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.RecordListItemResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.service.RecordService;
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

@Tag(name = "Record", description = "기록 API")
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "기록 목록 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공 (데이터 없을 경우 빈 배열 반환)")
    @GetMapping
    public ResponseEntity<PageResponse<RecordListItemResponse>> getRecords(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long explorationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        RecordSearchCondition condition = new RecordSearchCondition(categoryId, explorationId);
        return ResponseEntity.ok(recordService.getRecords(userId, condition, page, size));
    }

    @Operation(summary = "기록 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{recordId}")
    public ResponseEntity<CommonResponse<RecordDetailResponse>> getRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(CommonResponse.of(recordService.getRecord(userId, recordId)));
    }

    @Operation(summary = "기록 생성")
    @ApiResponse(responseCode = "201", description = "기록 생성 성공")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<CreateRecordResponse>> createRecord(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("request") @Valid CreateRecordRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(201).body(CommonResponse.of(recordService.createRecord(userId, request, images)));
    }

    @Operation(summary = "기록 수정")
    @ApiResponse(responseCode = "200", description = "기록 수정 성공")
    @PatchMapping(value = "/{recordId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<UpdateRecordResponse>> updateRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId,
            @RequestPart("request") @Valid UpdateRecordRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(CommonResponse.of(recordService.updateRecord(userId, recordId, request, images)));
    }

    @Operation(summary = "기록 삭제")
    @ApiResponse(responseCode = "200", description = "기록 삭제 성공")
    @DeleteMapping("/{recordId}")
    public ResponseEntity<CommonResponse<DeleteRecordResponse>> deleteRecord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(CommonResponse.of(recordService.deleteRecord(userId, recordId)));
    }
}
