package com.lightbc.templatej.utils;

import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Description: 通用工具类
 * Package:com.lightbc.templatej.utils
 *
 * @author lightbc
 * @version 1.0
 */
public class CommonUtil {

    /**
     * 获取UUID
     *
     * @return string uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");

    }

    /**
     * 获取不带拓展名的名称
     *
     * @return string
     */
    public static String getUUIDFileName() {
        return getUUIDFileName(null);
    }

    /**
     * 获取带拓展名的文件名称
     *
     * @param ext 拓展名
     * @return string
     */
    public static String getUUIDFileName(String ext) {
        String uuid = getUUID();
        if (StringUtils.isNotBlank(ext)) {
            uuid = uuid.concat(".").concat(ext);
        }
        return uuid;
    }

    /**
     * 获取默认格式的复制名称
     *
     * @param name 被复制的名称
     * @return string 新名称
     */
    public static String getDefaultCopyName(String name) {
        String ext = null;
        // 判断是否包含拓展名
        if (name.contains(".")) {
            ext = name.substring(name.indexOf("."));
            name = name.substring(0, name.indexOf("."));
        }
        // 含拓展名，默认格式：（原名称）-副本.（拓展名）
        if (ext != null) {
            return name.concat(ConfigInterface.DEFAULT_RENAME_SUFFIX).concat(ext);
        }
        // 默认格式：（原名称）-副本
        return name.concat(ConfigInterface.DEFAULT_RENAME_SUFFIX);
    }
}
