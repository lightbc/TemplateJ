package com.lightbc.templatej.utils;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.exception.NotExistException;
import com.lightbc.templatej.interfaces.ConfigInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.UUID;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {
    private static final String[] IMAGE_TYPES = new String[]{".png", ".jpg", ".jpeg", ".gif"};
    private File file;
    @Getter
    private File copyFile;

    public FileUtil() {

    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return boolean 存在：true，不存在：false
     */
    public boolean exist(String path) {
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
     * @return 是否创建成功-true：是，false：否
     */
    public boolean createFile(String path) {
        try {
            create(path, 1);
            return true;
        } catch (IOException e) {
            log.error("文件创建IO异常:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     * @return 是否创建成功-true：是，false：否
     */
    public boolean createDirs(String path) {
        try {
            create(path, 2);
            return true;
        } catch (IOException e) {
            log.error("文件夹创建IO异常:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 创建文件/文件夹
     *
     * @param path 文件路径
     * @param type 创建类型-文件：1，文件夹：2
     * @throws NotExistException 传值为空异常
     * @throws IOException       输入/输出异常
     */
    private void create(String path, int type) throws NotExistException, IOException {
        if (path == null || "".equals(path.trim())) {
            throw new NotExistException("需要创建的文件路径为空！");
        }
        file = new File(path);
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
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     * @throws NotExistException 不存在异常
     */
    public long getFileSize(File file) throws NotExistException {
        if (file == null || !file.exists()) {
            throw new NotExistException("文件尚未创建！");
        }
        if (!file.isFile()) {
            throw new NotExistException("非文件内容！");
        }
        return file.length();
    }

    /**
     * 格式化文件大小显示
     *
     * @param format 格式化格式-B:字节，K:千字节，M：兆字节，G：千兆字节
     * @param file   文件
     * @return 格式化后显示信息
     * @throws NotExistException 不存在异常
     */
    public String formatFileSize(String format, File file) throws NotExistException {
        return String.format("%.2f", getDivisor(format, getFileSize(file)));
    }

    /**
     * 获取格式化显示文件时，文件大小
     *
     * @param format 格式化格式
     * @param size   初始文件大小
     * @return 格式后文件大小
     * @throws NotExistException 不存在异常
     */
    private double getDivisor(String format, long size) throws NotExistException {
        if (format == null || "".equals(format)) {
            throw new NotExistException("文件大小格式化格式标准未传！");
        }
        double d = size;
        double divisor = 1024d;
        switch (format.toUpperCase()) {
            case "K":
                d = size / divisor;
                break;
            case "M":
                d = size / (divisor * divisor);
                break;
            case "G":
                d = size / (divisor * divisor * divisor);
                break;
        }
        return d;
    }

    /**
     * 读取文件内容
     *
     * @param path 读取文件路径
     * @return 文件内容
     */
    public synchronized String read(String path) throws NotExistException {
        if (path == null || "".equals(path.trim())) {
            throw new NotExistException("文件读取路径为空！");
        }
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        if (file.isFile()) {
            FileInputStream is = null;
            InputStreamReader reader = null;
            BufferedReader br = null;
            try {
                is = new FileInputStream(file);
                reader = new InputStreamReader(is, ConfigInterface.ENCODE_VALUE);
                br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            } catch (FileNotFoundException e) {
                log.error("文件未找到：{}", e.getMessage());
            } catch (IOException e) {
                log.error("文件读取过程出错：{}", e.getMessage());
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    log.error("文件读取关闭错误：{}", e.getMessage());
                }
            }
        } else {
            log.warn("读取内容非文件/读取文件不存在!");
        }
        return builder.toString();
    }

    /**
     * 文件写入内容
     *
     * @param path    写入文件路径
     * @param content 写入内容
     */
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
     * 删除文件/文件夹及包含
     *
     * @param file 需要删除的文件
     * @return 删除是否成功-true：成功，false：失败
     */
    public synchronized boolean delFile(File file) {
        if (!file.exists() || file == null) {
            log.warn("需要删除的文件夹/文件不存在！");
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    delFile(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
            return true;
        }
        if (file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param path   复制文件路径
     * @param rename 复制目标文件的文件夹/文件名称
     * @return 是否复制成功，成功：true，失败：false
     */
    public synchronized boolean copy(String path, String rename) {
        if (path == null || "".equals(path.trim())) {
            log.warn("重命名文件路径不存在!");
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            log.warn("文件/文件夹不存在！");
            return false;
        }
        //判断复制源文件是否是文件夹
        if (file.isDirectory()) {
            String dirPath = file.getAbsolutePath();
            String nDirPath = dirPath.substring(0, dirPath.lastIndexOf(File.separator)).concat(File.separator).concat(rename);
            File nFile = new File(nDirPath);
            if (nFile.exists()) {
                DialogUtil dialogUtil = new DialogUtil();
                int d = dialogUtil.showConfirmDialog(null, Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                if (d != 0) {
                    return false;
                }
                delFile(nFile);
            }
            //创建目标文件夹
            if (!nFile.exists()) {
                nFile.mkdirs();
            }
            //复制源文件夹下的所有文件
            copyFiles(file, dirPath, nDirPath);
            copyFile = file;
            return true;
        }
        //判断复制源文件是否是文件
        if (file.isFile()) {
            String name = file.getName();
            String ext = name.substring(name.indexOf("."));
            String filePath = file.getAbsolutePath();
            String nFilePath = filePath.substring(0, filePath.lastIndexOf(File.separator)).concat(File.separator).concat(rename).concat(ext);
            File nFile = new File(nFilePath);
            if (nFile.exists()) {
                DialogUtil dialogUtil = new DialogUtil();
                int d = dialogUtil.showConfirmDialog(null, Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                if (d != 0) {
                    return false;
                }
            }
            //创建目标文件
            if (!nFile.exists()) {
                try {
                    nFile.createNewFile();
                } catch (IOException e) {
                    log.error("重命名（创建新文件）失败：{}", e.getMessage());
                    return false;
                }
            }
            //复制源文件
            copyFile(filePath, nFilePath);
            copyFile = file;
            return true;
        }
        return false;
    }

    /**
     * 多个文件复制
     *
     * @param originDirFile 源文件对象
     * @param originDirPath 源文件路径
     * @param targetDirPath 目标文件路径
     * @return 是否复制成功，成功：true，失败：false
     */
    public boolean copyFiles(File originDirFile, String originDirPath, String targetDirPath) {
        try {
            File[] files = originDirFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    String filePath = file.getAbsolutePath();
                    String suffixPath = filePath.substring(originDirPath.length());
                    String tp = targetDirPath.concat(File.separator).concat(suffixPath);
                    //复制单个文件
                    copyFile(filePath, tp);
                }
            }
        } catch (Exception e) {
            log.error("文件夹及子项复制报错：{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 复制单个文件
     *
     * @param op 源文件路径
     * @param tp 目标文件路径
     * @return 文件复制是否成功，成功：true，失败：false
     */
    public synchronized boolean copyFile(String op, String tp) {
        try {
            String content = read(op);
            write(tp, content);
            return true;
        } catch (Exception e) {
            log.error("文件复制报错：{}", e.getMessage());
        }
        return false;
    }


    /**
     * 新增文件
     *
     * @param path    新增文件路径
     * @param content 新增文件内容
     * @param type    新增类型，文件-1，文件夹-2
     * @return 是否新增成功，成功：true，失败：false
     */
    public synchronized boolean add(String path, String content, int type) {
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
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace(new PrintWriter(error));
            builder.append("【文档加载错误1】：" + error).append("\n");
        } catch (Exception e) {
            e.printStackTrace(new PrintWriter(error));
            builder.append("【文档加载错误2】：" + error).append("\n");
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
                builder.append("【文档加载错误3】：" + error).append("\n");
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
    public static void cacheImage(String oPath, String savePath) {
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
            int len = 0;
            while ((len = bis.read(buffer, 0, 1024)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
    public static boolean isImage(String path) {
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
     * 获取随机生成的UUID名称
     *
     * @return string
     */
    public static String getUUIDName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取插件缓存地址
     *
     * @return string
     */
    public static String getPluginCacheDir() {
        String re = null;
        try {
            String pluginPath = PluginUtil.getPluginSavePath();
            if (pluginPath != null) {
                File pluginFile = new File(pluginPath);
                if (pluginFile.exists() && pluginFile.isDirectory()) {
                    String cacheDir = pluginPath.concat(File.separator).concat(ConfigInterface.PLUGIN_CACHE_DIR);
                    File cacheFile = new File(cacheDir);
                    if (!cacheFile.exists()) {
                        cacheFile.mkdir();
                    }
                    re = cacheFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return re;
        }
    }

}
