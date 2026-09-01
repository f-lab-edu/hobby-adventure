package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.WaypointImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WaypointImageMapper {

    @Insert("<script>INSERT INTO waypoint_images (waypoint_id, image_url, image_order) VALUES " +
            "<foreach collection='images' item='img' separator=','>(#{img.waypointId}, #{img.imageUrl}, #{img.imageOrder})</foreach></script>")
    void insertAll(@Param("images") List<WaypointImage> images);

    @Select("SELECT * FROM waypoint_images WHERE waypoint_id = #{waypointId} ORDER BY image_order ASC")
    List<WaypointImage> findAllByWaypointId(Long waypointId);

    @Select("<script>SELECT * FROM waypoint_images WHERE waypoint_id IN " +
            "<foreach collection='waypointIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            " ORDER BY image_order ASC</script>")
    List<WaypointImage> findAllByWaypointIds(@Param("waypointIds") List<Long> waypointIds);

    @Delete("DELETE FROM waypoint_images WHERE waypoint_id = #{waypointId}")
    void deleteAllByWaypointId(Long waypointId);

    @Delete("DELETE FROM waypoint_images WHERE id = #{imageId}")
    void deleteById(Long imageId);
}
