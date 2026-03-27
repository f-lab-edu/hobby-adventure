package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insert(User user);
}
