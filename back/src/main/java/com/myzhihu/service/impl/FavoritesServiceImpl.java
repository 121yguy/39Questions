package com.myzhihu.service.impl;

import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.AnswerDao;
import com.myzhihu.dao.FavoritesDao;
import com.myzhihu.dao.FavoritesFolderDao;
import com.myzhihu.domain.dto.FavoritesFolderWithFavoredStatus;
import com.myzhihu.domain.dto.FavoritesFolderWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.entity.FavoritesFolder;
import com.myzhihu.service.FavoritesService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    private FavoritesFolderDao favoritesFolderDao;

    @Autowired
    private FavoritesDao favoritesDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AnswerDao answerDao;

    @Override
    public int addFavoritesFolder(String name, int uid) {
        if (favoritesFolderDao.selectFolderIdByNameAndCreatorId(name, uid) != null) return 0;
        FavoritesFolder favoritesFolder = new FavoritesFolder();
        favoritesFolder.setName(name);
        favoritesFolder.setCreatorId(uid);
        favoritesFolderDao.createFavoritesFolder(favoritesFolder);
        return favoritesFolder.getId();
    }

    @Override
    public boolean deleteFavoritesFolder(int fid, int uid) {
        rabbitTemplate.convertAndSend(MQDeleteExchange.DELETE_EXCHANGE, MQDeleteExchange.DELETE_FAVOR_ROUTING, fid);
        return favoritesFolderDao.deleteFavoritesFolder(uid, fid) != 0;
    }

    @Override
    public boolean updateFavoritesFolderName(int fid, int uid, String newName) {
        if (favoritesFolderDao.selectFolderIdsByUserIdAndFolderId(uid, fid) == null) return false;
        favoritesFolderDao.updateFolderName(newName, fid);
        return true;
    }

    @Override
    public List<FavoritesFolder> getFavoritesFolder(int uid, int page) {
        int start = (page - 1) * 12;
        return favoritesFolderDao.selectFolderByUserId(uid, start);
    }

    @Override
    public List<FavoritesFolderWithFavoredStatus> getPersonalFavoritesFolder(int uid, int aid, int page) {
        int start = (page - 1) * 12;
        List<FavoritesFolder> favoritesFolders = favoritesFolderDao.selectFolderByUserId(uid, start);
        List<FavoritesFolderWithFavoredStatus> res = new ArrayList<>();
        for (FavoritesFolder favoritesFolder : favoritesFolders) {
            res.add(new FavoritesFolderWithFavoredStatus(favoritesFolder, favoritesDao.selectFolderIdsByAnswerIdAndFolderId(aid, favoritesFolder.getId()) != null));
        }
        return res;
    }

    @Override
    public List<HomePageQuestion> getFavoritesByFolderId(int fid, int page) {
        int start = (page - 1) * 12;
        return favoritesDao.selectFavoritesByFolderId(fid, start);
    }

    @Override
    public boolean doFavorite(int uid, int aid, Integer[] fids, boolean isFavorite) {
        if (answerDao.selectAnswerById(aid) == null) return false;
        List<Integer> newFids = favoritesFolderDao.folderIdFilter(fids, uid);
        if (newFids == null || newFids.isEmpty()) return false;
        if (isFavorite) favoritesDao.favorAll(newFids, aid);
        else favoritesDao.cancelFavorAll(newFids, aid);
        return true;
    }

    @Override
    public boolean checkFavored(int uid, int aid) {
        return favoritesDao.selectByUidAndAid(uid, aid).isEmpty();
    }

    @Override
    public FavoritesFolderWithUserInfo getFavoritesFolderInfo(int fid) {
        return favoritesFolderDao.selectFavoritesFolderWithUserInfoByFolderId(fid);
    }
}
