package com.myzhihu.domain.entity;

import lombok.Data;

@Data
public class FavoritesFolder {
    private int id;
    private int creatorId;
    private String name;
}
