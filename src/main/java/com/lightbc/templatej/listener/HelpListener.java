package com.lightbc.templatej.listener;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import com.lightbc.templatej.utils.PluginUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 帮助按钮事件监听
 */
@Slf4j
public class HelpListener {
    private static final String HELP = "/html/help.html";
    private static String HTML_CONTENT;
    private static ImageCache IMAGE_CACHE;

    public HelpListener() {

    }

    /**
     * 帮助事件监听功能
     *
     * @param button 帮助按钮
     */
    public void help(JButton button) {
        button.addActionListener(e -> {
            HTML_CONTENT = FileUtil.loadContent(HELP);
            showHelp();
        });
    }

    /**
     * 显示帮助对话框
     */
    private synchronized void showHelp() {
        DialogUtil dialogUtil = new DialogUtil();
        Map<String, ImageCache> map = cache();
        JScrollPane pane = getComponent(map);
        dialogUtil.showHelpDialog(Message.HELP.getTitle(), pane);
    }

    /**
     * 超链接事件监听
     *
     * @param editorPane 编辑面板
     */
    private void addHyperLinkListener(JEditorPane editorPane) {
        // 超链接事件监听
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                return;
            }
            // 超链接
            String href = e.getDescription();
            // 判断是否时网络链接，网络链接浏览器打开
            if (isNetAddress(href)) {
                openNetHyperLink(href);
            }
        });
    }

    /**
     * 缓存内容
     *
     * @return map
     */
    private Map<String, ImageCache> cache() {
        // 缓存图片工具
        IMAGE_CACHE = new ImageCache();
        Map<String, ImageCache> map = null;
        if (HTML_CONTENT != null && !"".equals(HTML_CONTENT.trim())) {
            // 获取文档内容里的所有图片路径
            List<String> src = getImagesPath(HTML_CONTENT);
            if (src.size() > 0) {
                map = new HashMap<>();
                for (String s : src) {
                    if (s.lastIndexOf("/") != -1) {
                        // 根据图片路径信息，获取文件名
                        String fileName = s.substring(s.lastIndexOf("/")).trim();
                        // 获取插件的缓存路径
                        String cachePath = FileUtil.getPluginCacheDir();
                        // 缓存文件保存路径
                        String path = cachePath.concat(File.separator).concat(fileName);
                        // 缓存图片
                        PluginUtil.cacheImage(s, path);
                        // 获取已缓存文件
                        File file = PluginUtil.getCacheFile(path);
                        ImageCache cache = new ImageCache();
                        Image image;
                        try {
                            URL url = file.toURI().toURL();
                            image = Toolkit.getDefaultToolkit().createImage(url);
                            cache.put(url, image);
                            map.put(s, cache);
                            IMAGE_CACHE.put(url, image);
                        } catch (MalformedURLException e) {
                            // 缓存失败错误内容提示
                            DialogUtil dialogUtil = new DialogUtil();
                            StringWriter writer = new StringWriter();
                            e.printStackTrace(new PrintWriter(writer));
                            dialogUtil.showTipsDialog(null, writer.toString(), "缓存失败");
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 打开网址
     *
     * @param path 网址链接
     */
    private void openNetHyperLink(String path) {
        DialogUtil dialogUtil = new DialogUtil();
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
        } catch (IOException e) {
            dialogUtil.showTipsDialog(null, e, "网址打开错误");
        }
    }

    /**
     * 获取编辑面板
     *
     * @param map 缓存图片替换
     * @return JScrollPane
     */
    @SuppressWarnings("UndesirableClassUsage")
    private synchronized JScrollPane getComponent(Map<String, ImageCache> map) {
        // 替换获取到的【help.html】中的图片路径内容成本地缓存路径
        if (HTML_CONTENT != null && !"".equals(HTML_CONTENT.trim()) && map != null && map.size() > 0) {
            for (String key : map.keySet()) {
                ImageCache cache = map.get(key);
                if (cache != null && cache.size() > 0) {
                    for (Object cKey : cache.keySet()) {
                        if (cKey instanceof URL) {
                            HTML_CONTENT = HTML_CONTENT.replace(key, cKey.toString());
                        }
                    }
                }
            }
        }
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setContentType("text/html");
        editorPane.setText(HTML_CONTENT);
        addHyperLinkListener(editorPane);

        Dictionary cache = (Dictionary) editorPane.getDocument().getProperty("imageCache");
        if (cache == null) {
            editorPane.getDocument().putProperty("imageCache", IMAGE_CACHE);
        }
        JPanel panel = new JPanel();
        panel.add(editorPane, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        // 解决初始化时滚动条不在顶部的问题
        SwingUtilities.invokeLater(() -> scrollBar.setValue(0));
        return scrollPane;
    }

    /**
     * 获取HTML字符串中的图片src信息
     *
     * @param htmlContent HTML内容
     * @return list<string>
     */
    private static List<String> getImagesPath(String htmlContent) {
        List<String> src = new ArrayList<>();
        String img;
        Pattern p_image;
        Matcher m_image;
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile
                (regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlContent);
        while (m_image.find()) {
            img = m_image.group();
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                src.add(m.group(1));
            }
        }
        return src;
    }

    /**
     * 判断是否是网络地址
     *
     * @param path 路径
     * @return boolean
     */
    private boolean isNetAddress(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    static class ImageCache extends Hashtable {

        public Object get(Object key) {

            Object result = super.get(key);

            if (result == null) {
                result = Toolkit.getDefaultToolkit().createImage((URL) key);
                put(key, result);
            }

            return result;
        }
    }

}
