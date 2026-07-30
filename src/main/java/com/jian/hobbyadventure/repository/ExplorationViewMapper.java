package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.ExplorationView;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExplorationViewMapper {

    @Insert("<script>" +
            "INSERT IGNORE INTO exploration_views (view_event_id, exploration_id, viewed_at) VALUES " +
            "<foreach collection='views' item='view' separator=','>" +
            "(#{view.viewEventId}, #{view.explorationId}, #{view.viewedAt})" +
            "</foreach>" +
            "</script>")
    void insertIgnoreBatch(@Param("views") List<ExplorationView> views);
}
