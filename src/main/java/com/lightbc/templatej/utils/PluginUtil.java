package com.lightbc.templatej.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.lightbc.templatej.interfaces.ConfigInterface;

import java.io.File;

/**
 * 插件工具类
 */
public class PluginUtil {

    /**
     * 获取插件的保存路径
     *
     * @return string
     */
    public static String getPluginSavePath() {
        //通过自己插件的id获取pluginId
        PluginId pluginId = PluginId.getId(ConfigInterface.PLUGIN_ID);
        if (pluginId != null) {
            IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
            File path = plugin.getPath();
            return path.getAbsolutePath();
        }
        return null;
    }

    /**
     * 缓存插件文件
     *
     * @param oPath    原文件路径
     * @param savePath 保存路径
     * @return boolean
     */
    public static boolean cacheImage(String oPath, String savePath) {
        if (savePath != null && oPath != null) {
            try {
                FileUtil.cacheImage(oPath, savePath);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取缓存文件
     *
     * @param path 文件路径
     * @return file
     */
    public static File getCacheFile(String path) {
        File file = null;
        if (path != null && !"".equals(path.trim())) {
            file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return file;
    }
}
