package com.myzhihu.controller;

import com.auth0.jwt.interfaces.Claim;
import com.myzhihu.domain.dto.FavoritesFolderWithFavoredStatus;
import com.myzhihu.domain.dto.FavoritesFolderWithUserInfo;
import com.myzhihu.domain.dto.HomePageQuestion;
import com.myzhihu.domain.dto.Result;
import com.myzhihu.domain.entity.FavoritesFolder;
import com.myzhihu.service.FavoritesService;
import com.myzhihu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    FavoritesService favoritesService;

    @PostMapping("/createFavoritesFolder")
    public Result<Integer> createFavoritesFolder(@CookieValue(value = "token", defaultValue = "")String token, @RequestParam("name") String name) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        int fid = favoritesService.addFavoritesFolder(name, uid);
        return fid == 0 ? Result.error("该文件夹名已存在") : Result.success(fid);
    }

    @PutMapping("/updateFolderName")
    public Result<Boolean> updateFolderName(@RequestParam("name") String newName, @RequestParam("fid") int fid, @CookieValue(value = "token", defaultValue = "")String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return favoritesService.updateFavoritesFolderName(fid, uid, newName) ? Result.success(true) : Result.error("这不是你的收藏夹或收藏夹不存在");
    }

    @GetMapping("/getFolders")
    public Result<List<FavoritesFolder>> getFolders(@RequestParam int uid, @RequestParam int page) {
        List<FavoritesFolder> favoritesFolders = favoritesService.getFavoritesFolder(uid, page);
        return Result.success(favoritesFolders);
    }

    @GetMapping("/getPersonalFolders")
    public Result<List<FavoritesFolderWithFavoredStatus>> getPersonalFolders(@CookieValue(value = "token", defaultValue = "")String token, @RequestParam("page") int page, @RequestParam("aid") int aid) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        List<FavoritesFolderWithFavoredStatus> res = favoritesService.getPersonalFavoritesFolder(uid, aid, page);
        return Result.success(res);
    }

    @GetMapping("/getFavorites")
    public Result<List<HomePageQuestion>> getFavorites(@RequestParam("fid") int fid, @RequestParam("page") int page) {
        List<HomePageQuestion> res = favoritesService.getFavoritesByFolderId(fid, page);
        return Result.success(res);
    }

    @PostMapping("/doFavor")
    public Result<Boolean> doFavor(int aid, Integer[] fids, @CookieValue(value = "token", defaultValue = "")String token, boolean isFavor) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return favoritesService.doFavorite(uid, aid, fids, isFavor) ? Result.success(true) : Result.error("收藏对象不存在或收藏夹不存在");
    }

    @GetMapping("/checkFavored")
    public Result<Boolean> checkFavored(@CookieValue(value = "token", defaultValue = "") String token, @RequestParam("aid") int aid) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        boolean status = favoritesService.checkFavored(uid, aid);
        return Result.success(status);
    }

    @GetMapping("/getFavoritesFolderInfo")
    public Result<FavoritesFolderWithUserInfo> getFavoritesFolderInfo(@RequestParam("fid") int fid) {
        return Result.success(favoritesService.getFavoritesFolderInfo(fid));
    }

    @DeleteMapping("/deleteFavoritesFolder")
    public Result<Boolean> deleteFavoritesFolder(@RequestParam("fid") int fid, @CookieValue(value = "token", defaultValue = "") String token) {
        Map<String, Claim> claims = JwtUtils.getJwtPayload(token);
        Integer uid = (Integer) claims.get("user").asMap().get("userId");
        return favoritesService.deleteFavoritesFolder(fid, uid) ? Result.success(true) : Result.error("这不是你的收藏夹");
    }

}
