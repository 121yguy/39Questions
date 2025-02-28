package com.myzhihu.consumer;

import com.myzhihu.constant.MQDeleteExchange;
import com.myzhihu.dao.ImageDao;
import com.myzhihu.utils.ImagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class DeleteImageConsumer {

    @Autowired
    private ImageDao imageDao;

    @Value("${imageFolder.imagesFolderPath}")
    private String imageFolderPath;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(MQDeleteExchange.DELETE_IMAGE_QUEUE),
                    exchange = @Exchange(MQDeleteExchange.DELETE_EXCHANGE)
            )
    )
    @RabbitHandler(isDefault = true)
    public void doDelete(String path) {
        String filename = ImagesUtils.getFilenameFromPath(path);
        File file = new File(imageFolderPath + filename);
        if (file.exists()) {
            if (!file.delete()) {
                log.warn("{}删除失败", filename);
            }
            else imageDao.deleteImageByPath(filename);
        }
        else {
            log.warn("{}不存在", filename);
            imageDao.deleteImageByPath(filename);
        }
    }
}
