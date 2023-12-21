package com.lightbc.templatej.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
     * 获取UUID，移除"-"
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
            uuid = uuid.concat(ext);
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

    /**
     * 判断是否是json对象数据
     *
     * @param s 判断字符串
     * @return Boolean true-json对象，false-非json对象
     */
    static boolean isJsonObject(String s) {
        boolean b = false;
        try {
            JSONObject.parseObject(s);
            b = true;
        } catch (Exception ignore) {
        } finally {
            return b;
        }
    }

    /**
     * 判断是否是json数组数据
     *
     * @param s 判断字符串
     * @return Boolean true-json数组，false-非json数组
     */
    static boolean isJsonArray(String s) {
        boolean b = false;
        try {
            JSONArray.parseArray(s);
            b = true;
        } catch (Exception ignore) {
        } finally {
            return b;
        }
    }

    /**
     * 对象是否为空验证
     *
     * @param o 验证对象
     * @return boolean 不为空：true，空：false
     */
    public static boolean validate(Object o) {
        if (o == null) {
            return false;
        }
        // 数组类型验证
        if (o instanceof List) {
            List list = (List) o;
            if (list.size() > 0) {
                return true;
            }
        }
        // 字符串类型验证
        if (o instanceof String) {
            String s = String.valueOf(o);
            return !"".equals(s.trim());
        }
        return false;
    }
}
