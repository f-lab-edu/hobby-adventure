package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE user_id = #{userId})")
    boolean existsById(Long userId);

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(String email);

    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE nickname = #{nickname})")
    boolean existsByNickname(String nickname);

    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    Optional<User> findById(Long userId);

    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    void deleteById(Long userId);

    @Insert("""
            INSERT INTO users (email, password, nickname)
            VALUES (#{email}, #{password}, #{nickname})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);
}
