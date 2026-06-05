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

    @Select("SELECT id FROM user_explorations WHERE user_id = #{userId}")
    List<Long> findIdsByUserId(Long userId);

    @Select("<script>SELECT id FROM user_explorations WHERE user_id = #{userId} AND exploration_id IN " +
            "<foreach collection='explorationIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Long> findIdsByUserIdAndExplorationIds(@Param("userId") Long userId, @Param("explorationIds") List<Long> explorationIds);

    @Select("<script>SELECT * FROM user_explorations WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<UserExploration> findByIdIn(@Param("ids") List<Long> ids);
}
