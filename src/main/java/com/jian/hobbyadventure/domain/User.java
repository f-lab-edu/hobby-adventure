package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private Long userId;
    private String email;
    private String password;
    private String nickname;

    public static User create(String email, String password, String nickname) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        return user;
    }

    // TODO: equals/hashCode는 필요해질 경우 기준 정의 후 추가
}
