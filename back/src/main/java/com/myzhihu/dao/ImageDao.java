package com.myzhihu.dao;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ImageDao {

    List<String> filterNonexistentFile(List<String> filenames);

    @Select("SELECT filename FROM image WHERE DATE(create_time) <= DATE(#{endTime}) AND reference_count <= 0")
    List<String> getImagesByDate(@Param("endTime") Date endTime);

    @Insert("INSERT INTO image(filename, create_time) VALUES (#{filename}, #{createTime})")
    int addImage(@Param("filename") String filename, @Param("createTime") Date createTime);

    @Delete("DELETE FROM image WHERE DATE(create_time) <= DATE(#{endTime}) AND reference_count <= 0")
    void deleteImagesByDate(Date endTime);

    @Delete("DELETE FROM image WHERE filename = #{filename}")
    void deleteImageByPath(String filename);

    void deleteImagesByPaths(List<String> filenames);

    @Update("UPDATE image SET reference_count = reference_count + 1 WHERE filename = #{filename}")
    void addReferenceCountByFilename(@Param("filename") String filename);

    @Update("UPDATE image SET reference_count = reference_count - 1 WHERE filename = #{filename}")
    void reduceReferenceCountByFilename(@Param("filename") String filename);

    void addReferenceCountByFilenames(@Param("filenames") List<String> filenames);

    void reduceReferenceCountByFilenames(@Param("filenames") List<String> filenames);

}
