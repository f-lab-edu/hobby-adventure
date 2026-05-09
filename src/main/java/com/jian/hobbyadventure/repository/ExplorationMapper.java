package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Exploration;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExplorationMapper {

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "category_id", javaType = Long.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "thumbnail_url", javaType = String.class),
            @Arg(column = "short_description", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "created_at", javaType = LocalDateTime.class)
    })
    @Select("SELECT id, category_id, title, thumbnail_url, short_description, description, created_at " +
            "FROM explorations ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Exploration> findAll(@Param("size") int size, @Param("offset") int offset);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "category_id", javaType = Long.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "thumbnail_url", javaType = String.class),
            @Arg(column = "short_description", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "created_at", javaType = LocalDateTime.class)
    })
    @Select("SELECT id, category_id, title, thumbnail_url, short_description, description, created_at " +
            "FROM explorations WHERE category_id = #{categoryId} ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Exploration> findByCategoryId(@Param("categoryId") Long categoryId, @Param("size") int size, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM explorations")
    long countAll();

    @Select("SELECT COUNT(*) FROM explorations WHERE category_id = #{categoryId}")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
