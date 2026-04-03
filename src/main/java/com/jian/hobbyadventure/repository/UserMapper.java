package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface UserMapper {

    @Insert("""
            INSERT INTO users (email, password, nickname)
            VALUES (#{email}, #{password}, #{nickname})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);
}
