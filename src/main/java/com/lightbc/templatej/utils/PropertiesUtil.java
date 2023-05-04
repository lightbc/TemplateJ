package com.lightbc.templatej.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 属性文件处理工具类
 */
public class PropertiesUtil {
    private Properties properties;
    private static final String PROPERTIES = "/TemplateJ.properties";

    public PropertiesUtil() {
        load();
    }

    /**
     * 初始化加载属性文件
     */
    private void load() {
        InputStream is = null;
        try {
            properties = new Properties();
            is = this.getClass().getResourceAsStream(PROPERTIES);
            properties.load(is);
            keyToUpperCase();
        } catch (IOException ignored) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 将属性文件中的key转换成全大写
     */
    private void keyToUpperCase() {
        Enumeration ep = properties.keys();
        Map<String, String> map = new HashMap<>();
        while (ep.hasMoreElements()) {
            String key = ep.nextElement().toString();
            String value = getValue(key);
            map.put(key.toUpperCase(), value);
            properties.remove(key);
        }
        properties.putAll(map);
    }

    /**
     * 设置属性
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 获取属性值
     *
     * @param key 键
     * @return string
     */
    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
