package com.lightbc.templatej.utils;

import javax.swing.*;

/**
 * Description:自定义icon工具类
 * Package:com.lightbc.templatej.utils
 *
 * @author lightbc
 * @version 1.0
 */
public class CustomIconUtil {
    private static final String ORIGIN_DIR = "/images/";

    /**
     * 获取图标对象，图标支持GIF、JPG、PNG格式。
     *
     * @param fileName 文件名称
     * @return icon 图标对象
     */
    public static Icon getIcon(String fileName) {
        String originPath = ORIGIN_DIR.concat(fileName);
        String cachePath = FileUtil.getPluginCacheFilePath(fileName);
        PluginUtil.cacheImage(originPath, cachePath);
        ImageIcon icon = new ImageIcon(cachePath);
        return icon;
    }


}
