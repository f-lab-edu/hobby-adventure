package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.Category;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @ConstructorArgs({
            @Arg(column = "category_id", javaType = Long.class),
            @Arg(column = "code", javaType = String.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "display_order", javaType = int.class)
    })
    @Select("SELECT category_id, code, name, display_order FROM categories ORDER BY display_order")
    List<Category> findAll();
}
