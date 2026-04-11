package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.dto.response.SignupResponse;
import com.jian.hobbyadventure.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", content = @Content, description = "입력값 검증 실패\n\n- 이메일: 누락 또는 형식 오류\n\n- 비밀번호: 누락 또는 규칙 위반 (8자 이상 32자 이하, 영문·숫자·특수문자 포함 필수)\n\n- 닉네임: 누락, 길이 위반 (2자 이상 12자 이하), 특수문자·공백 불가"),
            @ApiResponse(responseCode = "409", content = @Content, description = "이메일 또는 닉네임 중복")
    })
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.of(new SignupResponse(true)));
    }
}
