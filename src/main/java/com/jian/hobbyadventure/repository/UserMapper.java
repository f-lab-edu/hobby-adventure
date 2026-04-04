package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(String email);

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE nickname = #{nickname})")
    boolean existsByNickname(String nickname);

    @Insert("""
            INSERT INTO users (email, password, nickname)
            VALUES (#{email}, #{password}, #{nickname})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);
}
