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
    static String getPluginSavePath() {
        //通过自己插件的id获取pluginId
        PluginId pluginId = PluginId.getId(ConfigInterface.PLUGIN_ID);
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
        assert plugin != null;
        File path = plugin.getPath();
        return path.getAbsolutePath();
    }

    /**
     * 缓存插件文件
     *
     * @param oPath    原文件路径
     * @param savePath 保存路径
     */
    public static void cacheImage(String oPath, String savePath) {
        if (savePath != null && oPath != null) {
            try {
                FileUtil.cacheImage(oPath, savePath);
            } catch (Exception ignored) {
            }
        }
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
