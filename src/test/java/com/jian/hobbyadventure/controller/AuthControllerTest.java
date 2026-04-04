package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void signup_성공_시_201_응답을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", "닉네임");
        doNothing().when(userService).signup(any(SignupRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    void signup_이메일_누락_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest(null, "Abcd1234!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일을 입력해주세요."));
    }

    @Test
    void signup_이메일_형식_오류_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("invalid-email", "Abcd1234!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void signup_비밀번호_누락_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", null, "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."));
    }

    @Test
    void signup_비밀번호_길이_위반_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Ab1!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상 32자 이하로 입력해주세요."));
    }

    @Test
    void signup_비밀번호_문자_조건_위반_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd12345", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다."));
    }

    @Test
    void signup_닉네임_누락_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", null);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임을 입력해주세요."));
    }

    @Test
    void signup_닉네임_길이_위반_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", "닉");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 2자 이상 12자 이하로 입력해주세요."));
    }

    @Test
    void signup_닉네임_문자_위반_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", "닉네 임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 한글, 영문, 숫자만 사용할 수 있습니다."));
    }

    @Test
    void signup_이메일_중복_시_409를_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", "닉네임");
        doThrow(new BusinessException(ErrorCode.DUPLICATED))
                .when(userService).signup(any(SignupRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 값입니다."));
    }

    @Test
    void signup_닉네임_중복_시_409를_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!", "닉네임");
        doThrow(new BusinessException(ErrorCode.DUPLICATED))
                .when(userService).signup(any(SignupRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 값입니다."));
    }
}
