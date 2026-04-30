package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private final Long userId;
    private final String email;
    private final String nickname;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getUserId(), user.getEmail(), user.getNickname());
    }
}
