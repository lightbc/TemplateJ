package com.lightbc.templatej.utils;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.TemplateJInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板数据处理工具类型
 */
public class TemplateUtil {
    private List<Template> templates;

    public TemplateUtil(List<Template> templates) {
        this.templates = templates;
    }

    /**
     * 获取模板组名称
     *
     * @return list<string>
     */
    public List<String> getGroupNames() {
        List<String> list = new ArrayList<>();
        if (templates != null) {
            for (Template template : templates) {
                list.add(template.getGroupName());
            }
        }
        return list;
    }

    /**
     * 获取对应模板组模板文件名称
     *
     * @param groupName 模板组
     * @return list<string>
     */
    public List<String> getGroupFileNames(String groupName) {
        if (templates != null) {
            for (Template template : templates) {
                if (template.getGroupName().equals(groupName.trim())) {
                    return template.getGroupFiles();
                }
            }
        }
        return null;
    }

    /**
     * 获取指定模板的模板内容
     *
     * @param groupName     模板组
     * @param groupFileName 模板文件
     * @return string
     */
    public String getTemplateContent(String groupName, String groupFileName) {
        if (templates != null) {
            for (Template template : templates)
                if (template.getGroupName().equals(groupName.trim())) {
                    return template.getFileContentMap().getOrDefault(groupFileName, "");
                }
        }
        return "";
    }

    /**
     * 获取模板组全局配置信息
     *
     * @param groupName 模板组
     * @return string
     */
    public String getGlobalConfig(String groupName) {
        if (templates != null) {
            for (Template template : templates) {
                if (template.getGroupName().equals(groupName.trim())) {
                    return template.getGlobalConfig() != null ? template.getGlobalConfig() : "";
                }
            }
        }
        return "";
    }

    /**
     * 设置模板组全局配置信息
     *
     * @param groupName 模板组
     * @param content   全局配置信息
     */
    public void setGlobalConfig(String groupName, String content) {
        if (templates != null) {
            for (Template template : templates) {
                if (template.getGroupName().equals(groupName.trim())) {
                    template.setGlobalConfig(content);
                }
            }
        }
    }

    /**
     * 获取模板信息对象
     *
     * @param groupName 模板组名称
     * @return template
     */
    public Template getTemplate(String groupName) {
        if (templates != null) {
            for (Template template : templates) {
                if (template.getGroupName().equals(groupName.trim())) {
                    return template;
                }
            }
        }
        return null;
    }

    /**
     * 复制模板组
     *
     * @param rename   模板组名称
     * @param template 复制对象
     */
    public void copyGroup(String rename, Template template) {
        Template tp = new Template();
        tp.setGroupName(rename);
        tp.setGroupFiles(template.getGroupFiles());
        tp.setFileContentMap(template.getFileContentMap());
        tp.setGlobalConfig(template.getGlobalConfig());
        templates.add(tp);
    }

    /**
     * 复制模板文件
     *
     * @param copyGroupName 复制模板组名称
     * @param copyFileName  复制模板文件名称
     * @param rename        模板名称
     */
    public void copyGroupFile(String copyGroupName, String copyFileName, String rename) {
        if (templates != null) {
            for (int i = 0; i < templates.size(); i++) {
                Template template = templates.get(i);
                if (template.getGroupName().equals(copyGroupName.trim())) {
                    commonOperate(template, rename, template.getFileContentMap().get(copyFileName), i);
                    return;
                }
            }
        }
    }

    /**
     * 新增模板组
     *
     * @param rename 模板组名称
     */
    public void addGroup(String rename) {
        Template template = new Template();
        template.setGroupName(rename);
        List<String> groupFiles = new ArrayList<>();
        template.setGroupFiles(groupFiles);
        templates.add(template);
    }

    /**
     * 新增模板文件
     *
     * @param groupName 模板组名称
     * @param rename    模板文件名称
     * @param content   模板文件内容
     */
    public void addGroupFile(String groupName, String rename, String content) {
        Template template = getTemplate(groupName);
        int index = templates.indexOf(template);
        commonOperate(template, rename, content, index);
    }

    /**
     * 删除模板组
     *
     * @param groupName 删除模板名称
     */
    public void delGroup(String groupName) {
        Template template = getTemplate(groupName);
        templates.remove(template);
    }

    /**
     * 删除模板文件
     *
     * @param groupName 模板组名称
     * @param groupFile 删除模板文件名称
     */
    public void delGroupFile(String groupName, String groupFile) {
        // 获取删除模板对象
        Template template = getTemplate(groupName);
        // 获取删除对象数组下标
        int index = templates.indexOf(template);
        List<String> groupFiles = template.getGroupFiles();
        List<String> list = new ArrayList<>();
        for (String f : groupFiles) {
            if (!groupFile.trim().equals(f.trim())) {
                list.add(f);
            }
        }
        Map<String, String> fileContentMap = template.getFileContentMap();
        Map<String, String> map = new HashMap<>();
        for (String key : fileContentMap.keySet()) {
            if (!groupFile.trim().equals(key.trim())) {
                map.put(key, fileContentMap.get(key));
            }
        }
        // 获取新模板对象
        Template t = getNewTemplate(groupName, list, map, template.getGlobalConfig());
        // 删除旧模板对象
        templates.remove(index);
        // 根据删除模板对象下标，新增新的模板对象
        templates.add(index, t);
    }

    /**
     * 通用操作
     *
     * @param template 操作模板对象
     * @param name     操作对象名称（模板组/模板文件）
     * @param content  模板内容
     * @param index    操作模板对象原下标
     */
    private void commonOperate(Template template, String name, String content, int index) {
        List<String> groupFiles = template.getGroupFiles();
        Map<String, String> fileContentMap = template.getFileContentMap();
        Template t = getNewTemplate(template.getGroupName(), getNewGroupFiles(groupFiles, name), getNewFileContentMap(fileContentMap, name, content), template.getGlobalConfig());
        // 删除旧的
        templates.remove(index);
        // 添加新的
        templates.add(index, t);
    }

    /**
     * 获取新的模板对象
     *
     * @param groupName      模板组名称
     * @param groupFiles     模板文件
     * @param fileContentMap 模板文件内容
     * @param globalConfig   模板组全局配置信息
     * @return template
     */
    private Template getNewTemplate(String groupName, List<String> groupFiles, Map<String, String> fileContentMap, String globalConfig) {
        Template template = new Template();
        template.setGroupName(groupName);
        template.setGroupFiles(groupFiles);
        template.setFileContentMap(fileContentMap);
        template.setGlobalConfig(globalConfig);
        return template;
    }

    /**
     * 获取行的模板文件
     *
     * @param groupFiles 旧的模板文件
     * @param name       模板文件名称
     * @return list<string>
     */
    private List<String> getNewGroupFiles(List<String> groupFiles, String name) {
        List<String> list = new ArrayList<>();
        if (groupFiles != null) {
            list.addAll(groupFiles);
        }
        list.add(name);
        return list;
    }

    /**
     * 获取行的模板文件内容
     *
     * @param fileContentMap 旧的模板文件内容
     * @param name           模板文件名称
     * @param content        模板文件内容
     * @return map<string, string>
     */
    private Map<String, String> getNewFileContentMap(Map<String, String> fileContentMap, String name, String content) {
        Map<String, String> map = new HashMap<>();
        if (fileContentMap != null) {
            for (String key : fileContentMap.keySet()) {
                map.put(key, fileContentMap.get(key));
            }
            map.put(name, fileContentMap.get(content));
        }
        return map;
    }

    /**
     * 模板组是否已存在
     *
     * @param rename 新的模板组名称
     * @return boolean
     */
    public boolean existGroup(String rename) {
        return getTemplate(rename) != null;
    }

    /**
     * 模板文件是否已存在
     *
     * @param groupName 模板组名称
     * @param rename    新的模板文件名称
     * @return boolean
     */
    public boolean existGroupFile(String groupName, String rename) {
        List<String> groupFiles = getGroupFileNames(groupName);
        for (String name : groupFiles) {
            if (name.equals(rename.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取模板中的属性配置信息
     *
     * @param content 模板内容
     * @return map
     */
    private static Map<String, String> getTemplateJParams(String content) {
        Map<String, String> map = null;
        if (!"".equals(content.trim())) {
            map = new HashMap<>();
            boolean b = content.contains("\n");
            if (b) {
                String[] cts = content.split("\n");
                for (String s : cts) {
                    s = s.trim();
                    if (!"".equals(s.trim()) && s.startsWith("##")) {
                        String param = s.substring(2).trim();
                        if (param.contains(":")) {
                            String[] params = param.split(":");
                            map.put(params[0].trim().toUpperCase(), params[1].trim());
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 根据模板文件中的属性设置，重新设置属性默认值
     *
     * @param templateCode 模板内容
     * @param util         属性工具类
     */
    private static void setSettingProperties(String templateCode, PropertiesUtil util) {
        Map<String, String> map = getTemplateJParams(templateCode);
        if (map != null) {
            for (String key : map.keySet()) {
                util.set(key, map.get(key));
            }
        }
    }

    /**
     * 获取模板代码
     *
     * @param globalConfig 全局配置
     * @param templateCode 模板内容
     * @param util         属性工具类
     * @return string
     */
    public static String getSourceCode(String globalConfig, String templateCode, PropertiesUtil util) {
        setSettingProperties(templateCode, util);
        boolean b = Boolean.parseBoolean(util.getValue(TemplateJInterface.IGNORE_GLOBAL)) || Boolean.parseBoolean(util.getValue(TemplateJInterface.AUTO_PREVIEW));
        if (b) {
            if (!"".equals(templateCode.trim()) && templateCode.contains("\n")) {
                String[] tcs = templateCode.split("\n");
                StringBuilder builder = new StringBuilder();
                for (String s : tcs) {
                    s = s.trim();
                    if (!s.startsWith("##")) {
                        builder.append(s).append("\n");
                    }
                }
                return builder.toString().trim();
            }
        }
        return globalConfig.concat(templateCode).trim();
    }
}
