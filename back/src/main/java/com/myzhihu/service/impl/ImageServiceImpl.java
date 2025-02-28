package com.myzhihu.service.impl;

import com.myzhihu.aspect.DistributeScheduled;
import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.ImageDao;
import com.myzhihu.service.ImageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;

    @Value("${imageFolder.imagesFolderPath}")
    private String imageFolderPath;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void addImage(String filename) {
        imageDao.addImage(filename, new Date(System.currentTimeMillis()));
    }

    @Override
    @DistributeScheduled
    public void deleteExpiredImages() {
        Date ExpiredTime = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
        List<String> filenames = imageDao.getImagesByDate(ExpiredTime);
        if (filenames == null || filenames.isEmpty()) return;
        for (String filename : filenames) {
            rabbitTemplate.convertAndSend(MQDeleteExchange.DELETE_EXCHANGE, MQDeleteExchange.DELETE_IMAGE_ROUTING, filename);
        }
    }

    @Override
    public void deletePendingImagesByFilenames(List<String> filenames) {
        if (filenames == null || filenames.isEmpty())
            return;
        filenames = imageDao.filterNonexistentFile(filenames);
        if (filenames == null || filenames.isEmpty())
            return;
        for (String filename : filenames) {
            rabbitTemplate.convertAndSend(MQDeleteExchange.DELETE_EXCHANGE, MQDeleteExchange.DELETE_IMAGE_ROUTING, filename);
        }
        imageDao.deleteImagesByPaths(filenames);
    }

    @Override
    public void convertPendingImages2Images(List<String> filenames) {
        if (filenames != null && !filenames.isEmpty()) {
            imageDao.deleteImagesByPaths(filenames);
        }
    }

    @Override
    public void addReferenceCount(String filename) {
        Optional.ofNullable(filename).ifPresent(imageDao::addReferenceCountByFilename);
    }

    @Override
    public void reduceReferenceCount(String filename) {
        Optional.ofNullable(filename).ifPresent(imageDao::reduceReferenceCountByFilename);
    }

    @Override
    public void addReferenceCountByFilenames(List<String> filenames) {
        if (filenames != null && !filenames.isEmpty()) {
            imageDao.addReferenceCountByFilenames(filenames);
        }
    }

    @Override
    public void reduceReferenceCountByFilenames(List<String> filenames) {
        if (filenames != null && !filenames.isEmpty()) {
            imageDao.reduceReferenceCountByFilenames(filenames);
        }
    }
}
