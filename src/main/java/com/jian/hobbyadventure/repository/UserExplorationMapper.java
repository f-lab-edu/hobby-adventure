package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

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

    @Select("SELECT * FROM user_explorations WHERE id = #{id}")
    Optional<UserExploration> findById(Long id);

    @Update("UPDATE user_explorations SET status = 'COMPLETED', completed_at = NOW() WHERE id = #{id}")
    void complete(Long id);
}
