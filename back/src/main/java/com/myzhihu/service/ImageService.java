package com.myzhihu.service;

import java.util.List;

public interface ImageService {

    void addImage(String filename);

    void deleteExpiredImages();

    @Deprecated
    void deletePendingImagesByFilenames(List<String> filenames);

    @Deprecated
    void convertPendingImages2Images(List<String> filenames);

    void addReferenceCount(String filename);

    void reduceReferenceCount(String filename);

    void addReferenceCountByFilenames(List<String> filenames);

    void reduceReferenceCountByFilenames(List<String> filenames);

}
