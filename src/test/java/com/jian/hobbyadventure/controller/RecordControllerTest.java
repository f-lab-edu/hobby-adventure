package com.jian.hobbyadventure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Emotion;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.dto.response.CreateRecordResponse;
import com.jian.hobbyadventure.dto.response.DeleteRecordResponse;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.RecordListItemResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.service.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordController.class)
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecordService recordService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void getRecords_성공_시_200을_반환한다() throws Exception {
        List<RecordListItemResponse> data = List.of(
                new RecordListItemResponse(1L, 1L, 10L, "수채화 그리기 입문", 5L, "학습", "test title",
                        null, LocalDate.of(2025, 6, 1), 4, "HAPPY", "행복", LocalDateTime.now())
        );
        when(recordService.getRecords(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(PageResponse.of(data, PageMeta.of(1, 10, 1)));

        mockMvc.perform(get("/api/v1/records").header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("test title"))
                .andExpect(jsonPath("$.meta.totalElements").value(1));
    }

    @Test
    void createRecord_성공_시_201을_반환한다() throws Exception {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        MockMultipartFile requestPart = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));
        when(recordService.createRecord(anyLong(), any(), any())).thenReturn(new CreateRecordResponse(1L));

        mockMvc.perform(multipart("/api/v1/records").file(requestPart).header("X-User-Id", 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.recordId").value(1L));
    }

    @Test
    void createRecord_필수값_누락_시_400을_반환한다() throws Exception {
        CreateRecordRequest request = new CreateRecordRequest(1L, "", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        MockMultipartFile requestPart = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/v1/records").file(requestPart).header("X-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRecord_성공_시_200을_반환한다() throws Exception {
        RecordDetailResponse response = new RecordDetailResponse(
                1L, 1L, 10L, "수채화 그리기 입문", 5L, "학습", "test title",
                LocalDate.of(2025, 6, 1), 4, "HAPPY", "행복", null, "content",
                List.of(), LocalDateTime.now(), LocalDateTime.now()
        );
        when(recordService.getRecord(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/records/1").header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recordId").value(1L))
                .andExpect(jsonPath("$.data.title").value("test title"));
    }

    @Test
    void getRecord_존재하지_않으면_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND)).when(recordService).getRecord(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/records/1").header("X-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }

    @Test
    void getRecord_다른_유저_접근_시_403을_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(recordService).getRecord(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/records/1").header("X-User-Id", 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
    }

    @Test
    void updateRecord_성공_시_200을_반환한다() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(new UpdateRecordRequest("new title", null, null, null, null, null)));
        when(recordService.updateRecord(anyLong(), anyLong(), any(), any())).thenReturn(new UpdateRecordResponse(1L));

        mockMvc.perform(multipart("/api/v1/records/1").file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recordId").value(1L));
    }

    @Test
    void deleteRecord_성공_시_200을_반환한다() throws Exception {
        when(recordService.deleteRecord(anyLong(), anyLong())).thenReturn(new DeleteRecordResponse(1L));

        mockMvc.perform(delete("/api/v1/records/1").header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recordId").value(1L));
    }
}
