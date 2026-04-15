package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final Long userId;
    private final String nickname;

    public static LoginResponse from(User user) {
        return new LoginResponse(user.getUserId(), user.getNickname());
    }
}
