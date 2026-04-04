package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.ApiResponse;
import com.jian.hobbyadventure.dto.request.SignupRequest;
import com.jian.hobbyadventure.dto.response.SignupResponse;
import com.jian.hobbyadventure.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.of(new SignupResponse(true)));
    }
}
