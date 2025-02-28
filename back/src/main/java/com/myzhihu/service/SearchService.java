package com.myzhihu.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface SearchService<T> {
    List<T> doSearch(String keywords, int page, int numsPerPage);
    default List<String> keywordFilter(String keywords) {
        List<String> keywordList = Arrays.stream(keywords.split(" ")).filter(s -> !s.equals("%") && !s.equals("_")).toList();
        if (keywordList.isEmpty()) return Collections.emptyList();
        return keywordList;
    }
}
