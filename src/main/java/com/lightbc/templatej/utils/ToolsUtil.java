package com.lightbc.templatej.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 前端处理工具类型
 */
public class ToolsUtil {
    private static ToolsUtil toolsUtil;
    // 作者信息
    @Getter
    @Setter
    private String author;
    // 生成路径
    @Setter
    private String generatePath;

    public static ToolsUtil getInstance() {
        if (toolsUtil == null) {
            toolsUtil = new ToolsUtil();
        }
        return toolsUtil;
    }

    /**
     * 首字母小写
     *
     * @param s 原始命名
     * @return string 首字母小写
     */
    public String toLowerCamelCase(String s) {
        return StringUtils.uncapitalize(s);
    }

    /**
     * 首字母大写
     *
     * @param s 原始名称
     * @return string 首字母大写
     */
    public String toUpperCamelCase(String s) {
        return StringUtils.capitalize(s);
    }

    /**
     * 转全小写
     *
     * @param s 字符串
     * @return 全小写字符串
     */
    public String toLowerCase(String s) {
        return s.toLowerCase();
    }

    /**
     * 转全大写
     *
     * @param s 字符串
     * @return 全大写字符串
     */
    public String toUpperCase(String s) {
        return s.toUpperCase();
    }

    /**
     * 移除指定后缀
     *
     * @param s   字符串
     * @param chs 后缀字符串
     * @return string 处理后字符串
     */
    public String removeSuffix(String s, String chs) {
        return s.substring(0, s.lastIndexOf(chs));
    }

    /**
     * 移除指定前缀
     *
     * @param s   字符串
     * @param chs 前缀字符串
     * @return string 处理后的字符串
     */
    public String removePrefix(String s, String chs) {
        return s.substring(s.indexOf(chs) + chs.length());
    }

    /**
     * 字符替换
     *
     * @param s           需要进行替换的字符串
     * @param target      替换字符
     * @param replacement 替代字符
     * @return 替换后的新字符串
     */
    public String replace(String s, String target, String replacement) {
        return s.replace(target, replacement);
    }

    /**
     * 将多个字符依次拼接
     *
     * @param ss 拼接字符组
     * @return 拼接后的新字符
     */
    public String append(String... ss) {
        if (ss == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : ss) {
            if (s != null) {
                builder.append(s);
            }
        }
        return builder.toString();
    }

    /**
     * 字符串截取
     *
     * @param s          字符串
     * @param beginIndex 开始截取位置
     * @return 截取后字符串
     */
    public String substring(String s, int beginIndex) {
        return s.substring(beginIndex);
    }

    /**
     * 字符串截取
     *
     * @param s          字符串
     * @param beginIndex 开始位置
     * @param endIndex   结束位置
     * @return string 截取后字符串
     */
    public String substring(String s, int beginIndex, int endIndex) {
        return s.substring(beginIndex, endIndex);
    }

    /**
     * 获取指定字符首次出现下标
     *
     * @param s         字符串
     * @param indexChar 指定字符
     * @return int 下标位置
     */
    public int indexOf(String s, String indexChar) {
        return s.indexOf(indexChar);
    }

    /**
     * 获取指定字符最后一次出现下标
     *
     * @param s   字符串
     * @param str 指定字符
     * @return int 下标位置
     */
    public int lastIndexOf(String s, String str) {
        return s.lastIndexOf(str);
    }

    /**
     * 时间格式化
     *
     * @param pattern 格式化样式规则
     * @return string 格式化后时间
     */
    public String getDate(String pattern) {
        if (pattern == null || "".equals(pattern.trim())) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * 生成19位序列号（当前时间戳+随机数+L）
     *
     * @return string 序列号
     */
    public String serial() {
        StringBuilder builder = new StringBuilder();
        long time = System.currentTimeMillis();
        builder.append(time);
        Random random = new Random();
        while (builder.length() < 18) {
            int i = random.nextInt(10);
            builder.append(i);
        }
        builder.append("L");
        return builder.toString();
    }
}
