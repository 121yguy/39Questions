package com.myzhihu.dao;

import com.myzhihu.domain.dto.FavoritesFolderWithUserInfo;
import com.myzhihu.domain.entity.FavoritesFolder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoritesFolderDao {
    @Insert("INSERT INTO favorites_folder(name, creator_id) VALUES (#{name}, #{creatorId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createFavoritesFolder(FavoritesFolder favoritesFolder);

    @Update("UPDATE favorites_folder SET name = #{name} WHERE id = #{id}")
    void updateFolderName(@Param("name") String name, @Param("id") int id);

    @Select("SELECT id, name, creator_id AS creatorId FROM favorites_folder WHERE creator_id = #{creatorId} ORDER BY id LIMIT #{start}, 12")
    List<FavoritesFolder> selectFolderByUserId(@Param("creatorId") int creatorId, @Param("start") int start);

    @Select("SELECT id FROM favorites_folder WHERE creator_id = #{creatorId} AND id = #{id}")
    Integer selectFolderIdsByUserIdAndFolderId(@Param("creatorId") int creatorId, @Param("id") int id);

    @Select("SELECT favorites_folder.id FROM favorites_folder JOIN favorites ON favorites_folder.id = favorites.favorites_folder_id WHERE creator_id = #{uid} AND favorites.id = #{id}")
    FavoritesFolder selectFolderByCreatorIdAndFavoriteId(@Param("uid") int uid, @Param("id") int id);

    @Select("SELECT id FROM favorites_folder WHERE name = #{name} AND creator_id = #{uid}")
    Integer selectFolderIdByNameAndCreatorId(@Param("name") String name, @Param("uid") int uid);

    @Select("SELECT name, userId, icon, nickName FROM favorites_folder JOIN user_info ON favorites_folder.creator_id = user_info.userId WHERE favorites_folder.id = #{fid}")
    FavoritesFolderWithUserInfo selectFavoritesFolderWithUserInfoByFolderId(@Param("fid") int fid);

    @Delete("DELETE FROM favorites_folder WHERE creator_id = #{uid} AND id = #{fid}")
    int deleteFavoritesFolder(@Param("uid") int uid, @Param("fid") int fid);

    List<Integer> folderIdFilter(Integer[] fids, int uid);

}
