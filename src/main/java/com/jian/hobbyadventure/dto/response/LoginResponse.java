package com.jian.hobbyadventure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final Long userId;
    private final String nickname;
}
