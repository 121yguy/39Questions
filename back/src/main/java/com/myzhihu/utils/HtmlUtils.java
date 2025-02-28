package com.myzhihu.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

public class HtmlUtils {

    public static int getLength(String html) {
        return removeHtmlTags(html).trim().length();
    }

    public static String removeHtmlTags(String html) {
        if (Objects.isNull(html) || html.isEmpty()) {
            return "";
        }
        return Jsoup.parse(html).text();
    }

    public static String modifyImgWidth(String s, int x) {
        // 解析 HTML 内容
        Document doc = Jsoup.parse(s);

        // 获取所有的图片元素
        Elements images = doc.select("img");

        // 遍历所有图片元素并修改它们的内联样式
        for (Element img : images) {
            img.attr("style", "max-width: " + x + "px; height: auto;");
        }

        // 输出修改后的 HTML
        return doc.body().html();
    }

}
