package com.myzhihu.service;

import com.myzhihu.domain.dto.FavoritesFolderWithFavoredStatus;
import com.myzhihu.domain.dto.FavoritesFolderWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.FavoritesFolder;

import java.util.List;

public interface FavoritesService {
    int addFavoritesFolder(String name, int uid);
    boolean deleteFavoritesFolder(int fid, int uid);
    boolean updateFavoritesFolderName(int fid, int uid, String newName);
    List<FavoritesFolder> getFavoritesFolder(int uid, int page);
    List<FavoritesFolderWithFavoredStatus> getPersonalFavoritesFolder(int uid, int aid, int page);
    List<HomePageQuestion> getFavoritesByFolderId(int fid, int page);
    boolean doFavorite(int uid, int aid, Integer[] fids, boolean isFavorite);
    boolean checkFavored(int uid, int aid);
    FavoritesFolderWithUserInfo getFavoritesFolderInfo(int fid);
}
