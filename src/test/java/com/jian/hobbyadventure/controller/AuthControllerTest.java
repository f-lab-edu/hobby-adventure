package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.dto.request.LoginRequest;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.dto.response.LoginResponse;
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
import static org.mockito.Mockito.when;
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
    void login_성공_시_200_응답을_반환한다() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Abcd1234!");
        when(userService.login(any(LoginRequest.class))).thenReturn(new LoginResponse(1L, "닉네임"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"));
    }

    @Test
    void login_이메일_누락_시_400을_반환한다() throws Exception {
        LoginRequest request = new LoginRequest(null, "Abcd1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일을 입력해주세요."));
    }

    @Test
    void login_이메일_형식_오류_시_400을_반환한다() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "Abcd1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void login_비밀번호_누락_시_400을_반환한다() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", null);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."));
    }

    @Test
    void login_인증_실패_시_401을_반환한다() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Abcd1234!");
        doThrow(new BusinessException(ErrorCode.INVALID_CREDENTIALS))
                .when(userService).login(any(LoginRequest.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

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
                .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상이어야 합니다."));
    }

    @Test
    void signup_비밀번호_문자_조건_위반_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd12345", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 특수문자를 포함해야 합니다."));
    }

    @Test
    void signup_비밀번호_영문자_없을_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "12345678!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 영문자를 포함해야 합니다."));
    }

    @Test
    void signup_비밀번호_숫자_없을_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcdefgh!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 숫자를 포함해야 합니다."));
    }

    @Test
    void signup_비밀번호_32자_초과_시_400을_반환한다() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "Abcd1234!Abcd1234!Abcd1234!Abcd12!", "닉네임");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 32자 이하여야 합니다."));
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
