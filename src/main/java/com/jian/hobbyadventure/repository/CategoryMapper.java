package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM categories ORDER BY display_order")
    List<Category> findAll();

    @Select("SELECT * FROM categories WHERE category_id = #{categoryId}")
    Category findById(Long categoryId);

    @Select("SELECT EXISTS(SELECT 1 FROM categories WHERE category_id = #{categoryId})")
    boolean existsById(Long categoryId);
}
