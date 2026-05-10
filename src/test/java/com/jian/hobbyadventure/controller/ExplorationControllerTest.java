package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageMeta;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.response.ExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.ExplorationListItemResponse;
import com.jian.hobbyadventure.dto.response.StartExplorationResponse;
import com.jian.hobbyadventure.service.ExplorationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExplorationController.class)
class ExplorationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExplorationService explorationService;

    @Test
    void getExplorations_성공_시_200을_반환한다() throws Exception {
        List<ExplorationListItemResponse> data = List.of(
                new ExplorationListItemResponse(1L, "운동", "한강 러닝 챌린지", "/images/explorations/running.jpg", "한강변을 따라 5km 달려보세요")
        );
        PageMeta meta = PageMeta.of(1, 10, 1);
        when(explorationService.getExplorations(null, 1, 10)).thenReturn(PageResponse.of(data, meta));

        mockMvc.perform(get("/api/v1/explorations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("한강 러닝 챌린지"))
                .andExpect(jsonPath("$.meta.totalElements").value(1));
    }

    @Test
    void getExplorations_존재하지_않는_categoryId_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(explorationService).getExplorations(anyLong(), anyInt(), anyInt());

        mockMvc.perform(get("/api/v1/explorations").param("categoryId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }

    @Test
    void getExploration_성공_시_200을_반환한다() throws Exception {
        ExplorationDetailResponse response = new ExplorationDetailResponse(
                1L, "운동", "한강 러닝 챌린지", "/images/explorations/running.jpg",
                "한강변을 따라 5km 달려보세요", "상세 설명", LocalDateTime.now()
        );
        when(explorationService.getExploration(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/explorations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("한강 러닝 챌린지"));
    }

    @Test
    void getExploration_존재하지_않는_id_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(explorationService).getExploration(1L);

        mockMvc.perform(get("/api/v1/explorations/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }

    @Test
    void startExploration_성공_시_200을_반환한다() throws Exception {
        when(explorationService.startExploration(1L, 1L)).thenReturn(new StartExplorationResponse(10L));

        mockMvc.perform(post("/api/v1/explorations/1/start")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userExplorationId").value(10L));
    }

    @Test
    void startExploration_존재하지_않는_탐험_또는_사용자_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(explorationService).startExploration(anyLong(), anyLong());

        mockMvc.perform(post("/api/v1/explorations/1/start")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }
}
