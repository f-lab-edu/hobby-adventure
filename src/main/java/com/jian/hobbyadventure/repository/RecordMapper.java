package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Record;
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
public interface RecordMapper {

    @Insert("""
            INSERT INTO records (user_exploration_id, title, visited_date, rating, emotion_code, place_name, content)
            VALUES (#{userExplorationId}, #{title}, #{visitedDate}, #{rating}, #{emotionCode}, #{placeName}, #{content})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Record record);

    @Select("SELECT * FROM records WHERE id = #{id}")
    Optional<Record> findById(Long id);

    @Select("SELECT * FROM records WHERE user_exploration_id = #{userExplorationId}")
    Optional<Record> findByUserExplorationId(Long userExplorationId);

    @Select("SELECT EXISTS(SELECT 1 FROM records WHERE user_exploration_id = #{userExplorationId})")
    boolean existsByUserExplorationId(Long userExplorationId);

    @Select("<script>SELECT * FROM records WHERE user_exploration_id IN " +
            "<foreach collection='userExplorationIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            " ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}</script>")
    List<Record> findAllByUserExplorationIds(@Param("userExplorationIds") List<Long> userExplorationIds,
                                             @Param("size") int size,
                                             @Param("offset") int offset);

    @Select("<script>SELECT COUNT(*) FROM records WHERE user_exploration_id IN " +
            "<foreach collection='userExplorationIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    long countByUserExplorationIds(@Param("userExplorationIds") List<Long> userExplorationIds);

    @Update("""
            UPDATE records
            SET title = #{title}, visited_date = #{visitedDate}, rating = #{rating},
                emotion_code = #{emotionCode}, place_name = #{placeName}, content = #{content},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    void update(Record record);

    @Select("<script>SELECT user_exploration_id FROM records WHERE user_exploration_id IN " +
            "<foreach collection='userExplorationIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Long> findUserExplorationIdsByUserExplorationIdIn(@Param("userExplorationIds") List<Long> userExplorationIds);

    @Delete("DELETE FROM records WHERE id = #{id}")
    void deleteById(Long id);
}
