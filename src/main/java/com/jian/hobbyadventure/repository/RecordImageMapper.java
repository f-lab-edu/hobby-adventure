package com.jian.hobbyadventure.repository;

import com.jian.hobbyadventure.domain.RecordImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordImageMapper {

    @Insert("<script>INSERT INTO record_images (record_id, image_url, image_order) VALUES " +
            "<foreach collection='images' item='img' separator=','>(#{img.recordId}, #{img.imageUrl}, #{img.imageOrder})</foreach></script>")
    void insertAll(@Param("images") List<RecordImage> images);

    @Select("SELECT * FROM record_images WHERE record_id = #{recordId} ORDER BY image_order ASC")
    List<RecordImage> findAllByRecordId(Long recordId);

    @Select("<script>SELECT * FROM record_images WHERE record_id IN " +
            "<foreach collection='recordIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            " ORDER BY image_order ASC</script>")
    List<RecordImage> findAllByRecordIds(@Param("recordIds") List<Long> recordIds);

    @Delete("DELETE FROM record_images WHERE record_id = #{recordId}")
    void deleteAllByRecordId(Long recordId);
}
