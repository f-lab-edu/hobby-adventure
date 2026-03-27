package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insert_성공_시_userId가_채워진다() {
        User user = new User("test@example.com", "encodedPassword", "닉네임");

        userMapper.insert(user);

        assertThat(user.getUserId()).isNotNull();
    }

    @Test
    void insert_성공_시_DB에_저장된다() {
        User user = new User("test@example.com", "encodedPassword", "닉네임");

        userMapper.insert(user);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                "test@example.com"
        );
        assertThat(count).isEqualTo(1);
    }
}
