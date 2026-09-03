package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Waypoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface WaypointMapper {

    @Insert("""
            INSERT INTO waypoints (user_exploration_id, memo, place_name, checked_at)
            VALUES (#{userExplorationId}, #{memo}, #{placeName}, #{checkedAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Waypoint waypoint);

    @Select("SELECT * FROM waypoints WHERE id = #{id}")
    Optional<Waypoint> findById(Long id);

    @Select("""
            <script>
            SELECT * FROM waypoints WHERE user_exploration_id = #{userExplorationId}
            ORDER BY checked_at
            <choose>
                <when test="sortOrder == 'oldest'">ASC</when>
                <otherwise>DESC</otherwise>
            </choose>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<Waypoint> findAllByUserExplorationId(@Param("userExplorationId") Long userExplorationId,
                                              @Param("sortOrder") String sortOrder,
                                              @Param("size") int size,
                                              @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM waypoints WHERE user_exploration_id = #{userExplorationId}")
    long countByUserExplorationId(Long userExplorationId);

    @Update("""
            UPDATE waypoints
            SET memo = #{memo}, place_name = #{placeName}, checked_at = #{checkedAt}, updated_at = NOW()
            WHERE id = #{id}
            """)
    void update(Waypoint waypoint);

    @Delete("DELETE FROM waypoints WHERE id = #{id}")
    void deleteById(Long id);
}
