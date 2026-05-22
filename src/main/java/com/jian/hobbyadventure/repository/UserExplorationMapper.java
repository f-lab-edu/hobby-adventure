package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserExplorationMapper {

    @Insert("INSERT INTO user_explorations (user_id, exploration_id, status, created_at) VALUES (#{userId}, #{explorationId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserExploration userExploration);

    List<UserExploration> findAllByCondition(
            @Param("userId") Long userId,
            @Param("status") ExplorationStatus status,
            @Param("explorationIds") List<Long> explorationIds,
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countByCondition(
            @Param("userId") Long userId,
            @Param("status") ExplorationStatus status,
            @Param("explorationIds") List<Long> explorationIds
    );
}
