package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class User {

    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;

    // 회원가입용 — userId, createdAt은 DB에서 자동 생성
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // 조회용 — MyBatis <constructor> 매핑 시 사용
    public User(Long userId, String email, String password, String nickname, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    // TODO: equals/hashCode는 필요해질 경우 기준 정의 후 추가
}
