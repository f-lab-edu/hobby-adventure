package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "로그인 응답")
@Getter
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    public static LoginResponse from(User user) {
        return new LoginResponse(user.getUserId(), user.getNickname());
    }
}
