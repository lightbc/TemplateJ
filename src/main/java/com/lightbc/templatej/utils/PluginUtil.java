package com.lightbc.templatej.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
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
        } catch (Exception ignore) {
            try {
                File file = (File) util.getMethod(IdeaPluginDescriptor.class, "getPath").invoke(plugin, new Object[]{});
                path = (String) util.getMethod(File.class, "getAbsolutePath").invoke(file, new Object[]{});
            } catch (Exception ignore2) {
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

    /**
     * 保存API接口文档文件
     *
     * @param pdfName     pdf文件名称
     * @param htmlContent api文档内容
     * @param savePath    保存路径
     * @return boolean true-保存成功，false-保存失败
     */
    public static boolean saveApiDoc(String pdfName, String htmlContent, String savePath) {
        boolean rb = false;
        try {
            if (StringUtils.isNotBlank(savePath)) {
                // 缓存目录路径
                FileUtil fileUtil = new FileUtil();
                // 缓存文件路径
                String apiDocSavePath = savePath.concat(File.separator).concat(pdfName);
                fileUtil.createFile(apiDocSavePath);
                // 将HTML文档内容转换成PDF
                PdfUtil pdfUtil = new PdfUtil();
                boolean b = pdfUtil.htmlToPdf(apiDocSavePath, htmlContent);
                if (b) {
                    // 刷新工程目录结构
                    VirtualFileManager.getInstance().syncRefresh();
                    rb = true;
                }
            }
        } catch (Exception ignore) {
        } finally {
            return rb;
        }
    }

    /**
     * 缓存效果预览的api文档
     *
     * @param pdfName     pdf文件名称
     * @param htmlContent api文档内容
     */
    public static void cacheApiDoc(String pdfName, String htmlContent) {
        // 缓存目录路径
        FileUtil fileUtil = new FileUtil();
        String apiDocCacheDir = FileUtil.getPluginCacheDir().concat(File.separator).concat(ConfigInterface.API_DOC_CACHE_DIR);
        fileUtil.createDirs(apiDocCacheDir);
        // 缓存文件路径
        String apiDocCacheFilePath = apiDocCacheDir.concat(File.separator).concat(pdfName);
        fileUtil.createFile(apiDocCacheFilePath);
        // 将HTML文档内容转换成PDF
        PdfUtil pdfUtil = new PdfUtil();
        boolean b = pdfUtil.htmlToPdf(apiDocCacheFilePath, htmlContent);
        if (b) {
            String prefixCmd = "cmd /c ";
            // 首次尝试chrome浏览器打开展示PDF效果
            String cmd = prefixCmd + "start chrome.exe " + apiDocCacheFilePath;
            boolean r = runCmd(cmd);
            if (!r) {
                // chrome浏览器打开PDF失败，再次尝试系统PDF软件打开展示效果
                cmd = prefixCmd + "start " + apiDocCacheFilePath;
                boolean rr = runCmd(cmd);
                if (!rr) {
                    // 两次效果预览都失败后，弹窗提示，可手动尝试
                    DialogUtil dialog = new DialogUtil();
                    dialog.showTipsDialog(null, Message.API_DOC_PREVIEW_ERROR.getMsg(), Message.API_DOC_PREVIEW_ERROR.getTitle());
                }
            }

        }

    }

    /**
     * 执行cmd命令
     *
     * @param cmd 命令行
     * @return Boolean true-成功，false-失败
     */
    private static boolean runCmd(String cmd) {
        boolean b = false;
        try {
            Runtime.getRuntime().exec(cmd);
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return b;
        }
    }

    /**
     * 获取windows操作系统的字体文件存放目录路径
     *
     * @return string 字体目录路径
     */
    static String getWindowsFontsDir() {
        String path = System.getenv("WINDIR");
        File fontDir = new File(path, "Fonts");
        return fontDir.getAbsolutePath();
    }
}
