package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Exploration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExplorationMapper {

    @Update("UPDATE explorations SET view_count = view_count + #{delta} WHERE id = #{explorationId}")
    void incrementViewCount(@Param("explorationId") Long explorationId, @Param("delta") Long delta);

    @Select("SELECT * FROM explorations ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Exploration> findAll(@Param("size") int size, @Param("offset") int offset);

    @Select("SELECT * FROM explorations WHERE category_id = #{categoryId} ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Exploration> findAllByCategoryId(@Param("categoryId") Long categoryId, @Param("size") int size, @Param("offset") int offset);

    @Select("SELECT * FROM explorations WHERE id = #{id}")
    Optional<Exploration> findById(Long id);

    @Select("SELECT COUNT(*) FROM explorations")
    long countAll();

    @Select("SELECT COUNT(*) FROM explorations WHERE category_id = #{categoryId}")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT id FROM explorations WHERE category_id = #{categoryId}")
    List<Long> findIdsByCategoryId(@Param("categoryId") Long categoryId);

    @Select("<script>SELECT * FROM explorations WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Exploration> findByIdIn(@Param("ids") List<Long> ids);
}
