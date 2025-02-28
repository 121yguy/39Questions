package com.myzhihu.controller;

import com.myzhihu.domain.dto.Result;
import com.myzhihu.service.ImageService;
import com.myzhihu.utils.ImagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/pics")
public class ImageController {

    @Autowired
    ImageService imageService;

    @Value("${imageFolder.imagesFolderPath}")
    private String imageFolderPath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("文件无效");
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ".webp";
        String path = imageFolderPath + newName;
//        file.transferTo(new File(path));
        ImagesUtils.handleImage(file, path);
        imageService.addImage(newName);
        return Result.success("/images/" + newName);
    }

    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("文件无效");
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ".webp";
        String path = imageFolderPath + newName;
//        file.transferTo(new File(path));
        ImagesUtils.handleAvatar(file, path);
        imageService.addImage(newName);
        return Result.success("/images/" + newName);
    }

    @PostMapping("/uploadBackgroundImg")
    public Result<String> uploadBackgroundImg(MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("文件无效");
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ".webp";
        String path = imageFolderPath + newName;
//        file.transferTo(new File(path));
        ImagesUtils.handleBackgroundImg(file, path);
        imageService.addImage(newName);
        return Result.success("/images/" + newName);
    }

}
