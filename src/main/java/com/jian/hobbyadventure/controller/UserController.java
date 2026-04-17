package com.jian.hobbyadventure.controller;

import com.jian.hobbyadventure.common.response.CommonResponse;
import com.jian.hobbyadventure.dto.response.DeleteAccountResponse;
import com.jian.hobbyadventure.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse<DeleteAccountResponse>> deleteAccount(@PathVariable Long userId) {
        DeleteAccountResponse response = userService.deleteAccount(userId);
        return ResponseEntity.status(OK).body(CommonResponse.of(response));
    }
}
