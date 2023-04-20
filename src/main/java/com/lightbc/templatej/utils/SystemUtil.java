package com.lightbc.templatej.utils;

import lombok.Data;

/**
 * 操作系统信息获取工具类
 */
@Data
public class SystemUtil {
    // windows
    private static final String WINDOWS = "windows";
    // linux
    private static final String LINUX = "linux";
    // mac os
    private static final String MAC = "mac";
    // windows 换行符表示
    private static final String WINDOWS_LINE_SEPARATOR = "\r\n";
    // linux 换行符表示
    private static final String LINUX_LINE_SEPARATOR = "\n";
    // mac 换行符表示
    private static final String MAC_LINE_SEPARATOR = "\r";

    /**
     * 获取当前操作系统名称
     *
     * @return string
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /**
     * 判断是否时windows系统
     *
     * @return 是-true，否-false
     */
    public static boolean isWidows() {
        String osName = getOsName();
        return osName != null && osName.startsWith(WINDOWS);
    }

    /**
     * 判断是否是linux系统
     *
     * @return 是-true，否-false
     */
    public static boolean isLinux() {
        String osName = getOsName();
        return osName != null && osName.startsWith(LINUX);
    }

    /**
     * 判断是否是mac系统
     *
     * @return 是-true，否-false
     */
    public static boolean isMac() {
        String osName = getOsName();
        return osName != null && osName.startsWith(MAC);
    }

    /**
     * 获取当前操作系统的换行符表示
     *
     * @return string
     */
    public static String osLineSeparator() {
        return System.getProperty("line.separator");
    }

}
