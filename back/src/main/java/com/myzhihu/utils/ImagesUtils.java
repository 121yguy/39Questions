package com.myzhihu.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagesUtils {

    public static List<String> getFilenameFromHTML(String html) {
        List<String> res = new ArrayList<>();
        String imgTagPattern = "<img\\s+[^>]*src\\s*=\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(imgTagPattern);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String[] arr = matcher.group(1).split("/");
            res.add(arr[arr.length - 1]);
        }
        return res;
    }

    public static String getFilenameFromPath(String path) {
        String[] arr = path.split("/");
        return arr[arr.length - 1];
    }

    public static void getFilenamesFromPaths(List<String> paths) {
        for (int i = 0; i < paths.size(); i++) {
            String[] arr = paths.get(i).split("/");
            paths.set(i, arr[arr.length - 1]);
        }
    }

    public static void handleImage(MultipartFile file, String path) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        convertToWebp(image, path);
    }

    public static void handleAvatar(MultipartFile file, String path) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        convertToWebp(createAvatar(image), path);
    }

    public static void handleBackgroundImg(MultipartFile file, String path) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        convertToWebp(createBackground(image), path);
    }

    private static void convertToWebp(BufferedImage image, String path) {
        try {
            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // 设置有损压缩
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            //设置 80% 的质量. 设置范围 0-1
            writeParam.setCompressionQuality(0.8f);

            writer.setOutput(new FileImageOutputStream(new File(path)));
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // 创建 1:1 比例的头像（裁剪中心区域）
    private static BufferedImage createAvatar(BufferedImage image) {
        int size = Math.min(image.getWidth(), image.getHeight());  // 选择较小的边作为正方形边长
        int x = (image.getWidth() - size) / 2;  // 计算裁剪区域的 X 坐标
        int y = (image.getHeight() - size) / 2;  // 计算裁剪区域的 Y 坐标

        return image.getSubimage(x, y, size, size);  // 获取裁剪后的头像
    }


    // 创建 10:3 比例的背景图
    private static BufferedImage createBackground(BufferedImage image) {
        int targetWidth = image.getHeight() * 10 / 3;  // 根据高度计算目标宽度
        Image scaledImage = image.getScaledInstance(targetWidth, image.getHeight(), Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(targetWidth, image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // 使用 Graphics2D 进行图像绘制
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return newImage;
    }

}
