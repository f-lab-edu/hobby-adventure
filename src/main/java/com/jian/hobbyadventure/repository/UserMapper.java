package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(String email);

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE nickname = #{nickname})")
    boolean existsByNickname(String nickname);

    @ConstructorArgs({
            @Arg(column = "user_id", javaType = Long.class),
            @Arg(column = "email", javaType = String.class),
            @Arg(column = "password", javaType = String.class),
            @Arg(column = "nickname", javaType = String.class),
            @Arg(column = "created_at", javaType = LocalDateTime.class)
    })
    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    @Insert("""
            INSERT INTO users (email, password, nickname)
            VALUES (#{email}, #{password}, #{nickname})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);
}
