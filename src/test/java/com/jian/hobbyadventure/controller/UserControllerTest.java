package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.dto.response.DeleteAccountResponse;
import com.jian.hobbyadventure.dto.response.UserProfileResponse;
import com.jian.hobbyadventure.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getUserProfile_성공_시_200_응답을_반환한다() throws Exception {
        when(userService.getUserProfile(1L))
                .thenReturn(new UserProfileResponse(1L, "test@example.com", "닉네임"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"));
    }

    @Test
    void getUserProfile_존재하지_않는_userId_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(userService).getUserProfile(1L);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }

    @Test
    void deleteAccount_성공_시_200_응답을_반환한다() throws Exception {
        when(userService.deleteAccount(1L)).thenReturn(new DeleteAccountResponse(true));

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    void deleteAccount_존재하지_않는_userId_시_404를_반환한다() throws Exception {
        doThrow(new BusinessException(ErrorCode.NOT_FOUND))
                .when(userService).deleteAccount(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("요청하신 정보를 찾을 수 없습니다."));
    }
}
