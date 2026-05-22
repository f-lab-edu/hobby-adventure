package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Exploration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExplorationMapper {

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

    List<Exploration> findByIdIn(@Param("ids") List<Long> ids);
}
