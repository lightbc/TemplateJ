package com.lightbc.templatej.utils;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.lightbc.templatej.exception.NotExistException;
import com.lightbc.templatej.interfaces.ConfigInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {
    private static final String[] IMAGE_TYPES = new String[]{".png", ".jpg", ".jpeg", ".gif"};

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return boolean 存在：true，不存在：false
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    boolean exist(String path) {
        boolean f = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                f = true;
            }
        } catch (Exception e) {
            log.error("根据路径判断文件是否存在报错：{}", e.getMessage());
        } finally {
            return f;
        }
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     */
    public void createFile(String path) {
        try {
            create(path, 1);
        } catch (IOException e) {
            log.error("文件创建IO异常:{}", e.getMessage());
        }
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     */
    public void createDirs(String path) {
        try {
            create(path, 2);
        } catch (IOException e) {
            log.error("文件夹创建IO异常:{}", e.getMessage());
        }
    }

    /**
     * 创建文件/文件夹
     *
     * @param path 文件路径
     * @param type 创建类型-文件：1，文件夹：2
     * @throws NotExistException 传值为空异常
     * @throws IOException       输入/输出异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void create(String path, int type) throws NotExistException, IOException {
        if (path == null || "".equals(path.trim())) {
            throw new NotExistException("需要创建的文件路径为空！");
        }
        File file = new File(path);
        //判断文件夹/文件是否存在
        if (!file.exists()) {
            if (type == 1) {
                //创建文件
                file.createNewFile();
            }
            if (type == 2) {
                //创建文件夹
                file.mkdirs();
            }
        }
    }

    /**
     * 读取文件内容
     *
     * @param path 文件路径
     * @return string 文件内容
     */
    public synchronized String read(String path) {
        if (path == null || "".equals(path.trim())) {
            throw new NotExistException("文件读取路径为空！");
        }
        File file = new File(path);
        if (file.isDirectory()) {
            throw new NotExistException("读取路径非文件格式!");
        }
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, ConfigInterface.ENCODE_VALUE);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            log.error("读取文件未找到：{}", e.getMessage());
        } catch (IOException e) {
            log.error("读取文件报错：{}", e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("文件输入流关闭报错：{}", e.getMessage());
            }
            return builder.toString();
        }
    }

    /**
     * 文件写入内容
     *
     * @param path    写入文件路径
     * @param content 写入内容
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized void write(String path, String content) {
        if (path == null || "".equals(path.trim())) {
            throw new NotExistException("文件写入路径为空！");
        }
        File file = new File(path);
        if (file.isDirectory()) {
            throw new NotExistException("保存路径为文件目录，非文件路径!");
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("创建新文件出错：{}", e.getMessage());
            }
        }
        FileOutputStream os = null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
            os = new FileOutputStream(file);
            writer = new OutputStreamWriter(os, ConfigInterface.ENCODE_VALUE);
            bw = new BufferedWriter(writer);
            bw.write(content);
            bw.newLine();
            bw.flush();
        } catch (FileNotFoundException e) {
            log.error("输出文件未找到：{}", e.getMessage());
        } catch (IOException e) {
            log.error("文件输出过程错误：{}", e.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.error("文件输出关闭错误：{}", e.getMessage());
            }
        }
    }


    /**
     * 新增文件
     *
     * @param path    新增文件路径
     * @param content 新增文件内容
     * @param type    新增类型，文件-1，文件夹-2
     * @return 是否新增成功，成功：true，失败：false
     */
    public boolean add(String path, String content, int type) {
        //文件创建
        try {
            create(path, type);
            if (type == 1) {
                write(path, content);
            }
            return true;
        } catch (IOException e) {
            log.error("文件创建失败：{}", e.getMessage());
        }
        return false;
    }

    /**
     * 加载显示内容
     *
     * @param path 文件路径
     * @return string
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public static String loadContent(String path) {
        StringBuilder builder = new StringBuilder();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        StringWriter error = new StringWriter();
        try {
            is = getInputStream(path);
            isr = new InputStreamReader(is, ConfigInterface.ENCODE_VALUE);
            reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace(new PrintWriter(error));
            builder.append("【文档加载错误1】：").append(error).append("\n");
        } catch (Exception e) {
            e.printStackTrace(new PrintWriter(error));
            builder.append("【文档加载错误2】：").append(error).append("\n");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace(new PrintWriter(error));
                builder.append("【文档加载错误3】：").append(error).append("\n");
            }
            return builder.toString();
        }
    }

    /**
     * 缓存图片
     *
     * @param oPath    图片原路径
     * @param savePath 图片缓存路径
     */
    synchronized static void cacheImage(String oPath, String savePath) {
        if (!isImage(oPath)) {
            return;
        }
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            is = getInputStream(oPath);
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(savePath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer, 0, 1024)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否为指定图片格式
     *
     * @param path 路径
     * @return boolean
     */
    private static boolean isImage(String path) {
        if (path != null && !"".equals(path.trim())) {
            if (path.lastIndexOf(".") != -1) {
                String ext = path.substring(path.lastIndexOf("."));
                for (String type : IMAGE_TYPES) {
                    if (type.equals(ext.trim().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取输入流
     *
     * @param path 读取文件路径
     * @return inputstream
     */
    private static InputStream getInputStream(String path) {
        return FileUtil.class.getResourceAsStream(path);
    }

    /**
     * 获取插件缓存地址
     *
     * @return string
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock", "ResultOfMethodCallIgnored"})
    static String getPluginCacheDir() {
        String re = null;
        try {
            String pluginPath = PluginUtil.getPluginSavePath();
            assert pluginPath != null;
            File pluginFile = new File(pluginPath);
            if (pluginFile.exists() && pluginFile.isDirectory()) {
                String cacheDir = pluginPath.concat(File.separator).concat(ConfigInterface.PLUGIN_CACHE_DIR);
                File cacheFile = new File(cacheDir);
                if (!cacheFile.exists()) {
                    cacheFile.mkdir();
                }
                re = cacheFile.getAbsolutePath();
            }
        } catch (Exception ignore) {
        } finally {
            return re;
        }
    }

    /**
     * 获取插件缓存图片文件路径
     *
     * @param fileName 文件名
     * @return string 文件路径
     */
    public static String getPluginCacheImagesFilePath(String fileName) {
        String imageDir = getPluginCacheDir().concat(File.separator).concat(ConfigInterface.IMAGES_CACHE_DIR);
        new FileUtil().createDirs(imageDir);
        return imageDir.concat(File.separator).concat(fileName);
    }

    /**
     * 获取插件配置文件路径
     *
     * @return string 配置文件路径
     */
    public String getPluginConfigFilePath() {
        String re = null;
        try {
            // 获取插件安装目录
            String pluginPath = PluginUtil.getPluginSavePath();
            if (StringUtils.isNotBlank(pluginPath)) {
                // 获取插件安装父级目录
                File pluginFile = new File(pluginPath);
                if (pluginFile.exists() && pluginFile.isDirectory()) {
                    String parentPath = pluginFile.getParent();
                    re = parentPath.concat(File.separator).concat(ConfigInterface.PLUGIN_CONFIG_FILENAME);
                    // 在插件安装同级目录下创建插件配置文件，避免插件更新升级时，自定义模板文件丢失问题
                    File cacheFile = new File(re);
                    if (!cacheFile.exists()) {
                        cacheFile.createNewFile();
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return re;
        }
    }

    /**
     * 文件是否为指定类型判断
     *
     * @param filePath  文件路径
     * @param extension 拓展名
     * @return Boolean true-指定类型，false-非指定类型
     */
    static boolean isFileType(String filePath, String extension) {
        boolean b = false;
        try {
            // 判断文件路径和指定拓展名是否为空
            if (StringUtils.isNotBlank(filePath) && StringUtils.isNotBlank(extension)) {
                // 判断文件是否存在
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    // 判断文件的拓展名和指定拓展名是否相同
                    String filePathExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
                    if (filePathExtension.equals(extension)) {
                        b = true;
                    }
                }
            }
        } catch (Exception ignore) {
        } finally {
            return b;
        }
    }

    /**
     * 根据路径判断是否是文件
     *
     * @param path 文件路径
     * @return boolean 是-true，false-否
     */
    public static boolean isFile(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 根据路径判断是否是文件夹
     *
     * @param path 文件路径
     * @return boolean true-是，false-否
     */
    public static boolean isDir(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 动态匹配分隔符
     *
     * @param path 文件路径
     * @return string 路径分隔符
     */
    public static String getSeparator(String path) {
        if (path != null && path.lastIndexOf("/") == -1) {
            return "\\";
        }
        return "/";
    }

    /**
     * 获取指定文件类型的虚拟文件路径
     *
     * @param ext 拓展名
     * @return string 文件路径
     */
    public static String getVirtualFilePathByFileType(String ext) {
        return getPath(0, ext);
    }

    /**
     * 获取虚拟文件文件夹目录路径
     *
     * @return string 文件目录路径
     */
    public static String getVirtualFileDir() {
        return getPath(1);
    }

    /**
     * 获取虚拟文件/目录的路径
     *
     * @return string 路径
     */
    public static String getVirtualFilePathOrDir() {
        return getPath(2);
    }

    /**
     * 获取指定类型的路径
     *
     * @param type 0：指定文件类型文件路径，1：文件目录路径，2：文件/目录的路径（默认）
     * @return string 路径
     */
    private static String getPath(int type) {
        return getPath(type, null);
    }

    /**
     * 获取指定类型的路径
     *
     * @param type 0：指定文件类型文件路径，1：文件目录路径，2：文件/目录的路径（默认）
     * @param ext  文件拓展名
     * @return string 路径
     */
    private static String getPath(int type, String ext) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor();
        switch (type) {
            case 0:
                descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(ext);
                break;
            case 1:
                descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                break;
        }
        VirtualFile vf = FileChooser.chooseFile(descriptor, ProjectUtil.getProject(), getVirtualFile());
        return vf != null ? vf.getPath() : null;
    }

    /**
     * 获取虚拟文件对象
     *
     * @return virtualfile
     */
    private static VirtualFile getVirtualFile() {
        return ProjectUtil.getProject().getProjectFile();
    }

}
