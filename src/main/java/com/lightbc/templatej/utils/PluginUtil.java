package com.lightbc.templatej.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;

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
        IdeaPluginDescriptor[] plugins = PluginManager.getPlugins();
        for (IdeaPluginDescriptor plugin : plugins) {
            if (ConfigInterface.PLUGIN_ID.equals(plugin.getPluginId().getIdString())) {
                return adaptVersion(plugin);
            }
        }
        return null;
    }

    /**
     * 版本适应
     *
     * @param plugin idea插件描述
     * @return 缓存路径
     */
    private static String adaptVersion(IdeaPluginDescriptor plugin) {
        ReflectUtil util = new ReflectUtil();
        String path = null;
        try {
            Path filePath = (Path) util.getMethod(IdeaPluginDescriptor.class, "getPluginPath").invoke(plugin, new Object[]{});
            path = filePath.toFile().getAbsolutePath();
        } catch (Exception e) {
            try {
                File file = (File) util.getMethod(IdeaPluginDescriptor.class, "getPath").invoke(plugin, new Object[]{});
                path = (String) util.getMethod(File.class, "getAbsolutePath").invoke(file, new Object[]{});
            } catch (Exception e1) {
            }
        } finally {
            return path;
        }
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
        if (StringUtils.isNotBlank(path)) {
            file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return file;
    }
}
