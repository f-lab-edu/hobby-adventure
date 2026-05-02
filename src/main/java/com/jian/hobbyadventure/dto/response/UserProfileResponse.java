package com.jian.hobbyadventure.dto.response;

import com.jian.hobbyadventure.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자 정보 조회 응답")
@Getter
@AllArgsConstructor
public class UserProfileResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getUserId(), user.getEmail(), user.getNickname());
    }
}
