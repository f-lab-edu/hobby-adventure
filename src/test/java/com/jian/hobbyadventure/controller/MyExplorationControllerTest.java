package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.service.MyExplorationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyExplorationController.class)
class MyExplorationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyExplorationService myExplorationService;

    @Test
    void getMyExplorations_성공_시_200을_반환한다() throws Exception {
        List<MyExplorationListItemResponse> data = List.of(
                new MyExplorationListItemResponse(1L, 10L, "탐험 제목", "/images/test.jpg", 1L, "운동", "짧은 설명", ExplorationStatus.STARTED, LocalDateTime.now(), null, null)
        );
        when(myExplorationService.getMyExplorations(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(PageResponse.of(data, PageMeta.of(1, 10, 1)));

        mockMvc.perform(get("/api/v1/my-explorations")
                        .header("X-User-Id", 1L)
                        .param("status", "STARTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("탐험 제목"))
                .andExpect(jsonPath("$.meta.totalElements").value(1));
    }

    @Test
    void getMyExplorations_status_없으면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/my-explorations")
                        .header("X-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMyExploration_성공_시_200을_반환한다() throws Exception {
        MyExplorationDetailResponse response = new MyExplorationDetailResponse(
                1L, 10L, "탐험 제목", "/images/test.jpg", 1L, "운동", "짧은 설명",
                ExplorationStatus.STARTED, LocalDateTime.now(), null, null, null
        );
        when(myExplorationService.getMyExploration(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/my-explorations/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userExplorationId").value(1L));
    }

    @Test
    void getMyExploration_존재하지_않는_id_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(myExplorationService).getMyExploration(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/my-explorations/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyExploration_다른_사용자의_탐험_시_403을_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(myExplorationService).getMyExploration(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/my-explorations/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void completeExploration_성공_시_200을_반환한다() throws Exception {
        when(myExplorationService.completeExploration(1L, 1L)).thenReturn(new CompleteExplorationResponse(1L));

        mockMvc.perform(patch("/api/v1/my-explorations/1/complete")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userExplorationId").value(1L));
    }

    @Test
    void completeExploration_이미_완료된_탐험_시_409를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.INVALID_STATE))
                .when(myExplorationService).completeExploration(anyLong(), anyLong());

        mockMvc.perform(patch("/api/v1/my-explorations/1/complete")
                        .header("X-User-Id", 1L))
                .andExpect(status().isConflict());
    }
}
