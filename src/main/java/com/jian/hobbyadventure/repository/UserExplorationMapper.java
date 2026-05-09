package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.UserExploration;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface UserExplorationMapper {

    @Insert("INSERT INTO user_explorations (user_id, exploration_id, status, created_at) " +
            "VALUES (#{userId}, #{explorationId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserExploration userExploration);
}
