package com.myzhihu.dao;

import com.myzhihu.domain.dto.HomePageQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface FavoritesDao {

    @Insert("INSERT INTO favorites(answer_id, favorites_folder_id) VALUES (#{aid}, #{fid})")
    void favorite(@Param("aid") int aid, @Param("fid") int fid);

    @Delete("DELETE FROM favorites WHERE answer_id = #{aid} AND favorites_folder_id = #{fid}")
    void cancelFavoriteByAidAndFid(@Param("aid") int aid, @Param("fid") int fid);

    @Delete("DELETE FROM favorites WHERE favorites_folder_id = #{fid}")
    void deleteFavoritesByFid(@Param("fid") int fid);

    List<HomePageQuestion> selectFavoritesByFolderId(@Param("fid") int fid, @Param("start") int start);

    @Select("SELECT favorites_folder.id FROM favorites_folder JOIN favorites f on favorites_folder.id = f.favorites_folder_id WHERE favorites_folder.creator_id = #{uid} AND answer_id = #{aid}")
    List<Integer> selectByUidAndAid(@Param("uid") int uid, @Param("aid") int aid);

    @Select("SELECT favorites_folder_id FROM favorites_folder JOIN my_zhihu_db.favorites f on favorites_folder.id = f.favorites_folder_id WHERE answer_id = #{aid} AND favorites_folder_id = #{fid}")
    Integer selectFolderIdsByAnswerIdAndFolderId(@Param("aid") int aid, @Param("fid") int fid);

    @MapKey("answer_id")
    Map<Integer, Boolean> batchSelectByUidAndAid(@Param("answerIds") List<Integer> answerIds, @Param("userId") int userId);

    void favorAll(List<Integer> fids, int aid);

    void cancelFavorAll(List<Integer> fids, int aid);
}
