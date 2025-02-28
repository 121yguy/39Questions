package com.myzhihu.domain.dto;

import com.myzhihu.domain.entity.FavoritesFolder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoritesFolderWithFavoredStatus {

    private FavoritesFolder favoritesFolder;
    private boolean favoredStatus;

}
