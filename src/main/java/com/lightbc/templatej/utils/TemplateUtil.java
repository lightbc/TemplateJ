package com.lightbc.templatej.utils;

import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.interfaces.TemplateJInterface;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板数据处理工具类型
 */
public class TemplateUtil {
    // 模板组
    private List<Template> templates;
    @Getter
    private PropertiesUtil propUtil;

    public TemplateUtil(List<Template> templates) {
        this.templates = templates;
    }

    /**
     * 判断是否是模板文件
     *
     * @param selectGroupFile 选择项
     * @return boolean true-是，false-否
     */
    public static boolean isTemplateFile(Object selectGroupFile) {
        return selectGroupFile != null && !selectGroupFile.toString().equals(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
    }

    /**
     * 获取模板组名称
     *
     * @return list<string>
     */
    public List<String> getGroupNames() {
        List<String> list = null;
        if (this.templates != null) {
            list = new ArrayList<>();
            for (Template template : this.templates) {
                if (StringUtils.isNotBlank(template.getGroupName())) {
                    list.add(template.getGroupName());
                }
            }
        }
        return list;
    }

    /**
     * 获取模板文件
     *
     * @param template 模板
     * @return list<string>
     */
    public List<String> getGroupFileNames(Template template) {
        return template != null ? template.getGroupFiles() : null;
    }

    /**
     * 通过模板组名称获取模板文件
     *
     * @param groupName 模板组名称
     * @return list<string>
     */
    public List<String> getGroupFileNames(String groupName) {
        Template template = getTemplate(groupName);
        return getGroupFileNames(template);
    }

    /**
     * 获取模板文件内容
     *
     * @param template      模板
     * @param groupFileName 模板文件名称
     * @return string 模板内容
     */
    public String getTemplateContent(Template template, String groupFileName) {
        if (template != null && StringUtils.isNotBlank(groupFileName)) {
            Map<String, String> contentMap = template.getFileContentMap();
            if (contentMap != null && contentMap.containsKey(groupFileName)) {
                return template.getFileContentMap().get(groupFileName);
            }
        }
        return null;
    }

    /**
     * 获取模板配置的类型映射数据
     *
     * @param template 模板
     * @return object[][]
     */
    Object[][] getTypeMapper(Template template) {
        return template != null ? template.getTypeMapper() : null;
    }

    /**
     * 获取jdbcType类型映射数据
     *
     * @param template 模板
     * @return object[][]
     */
    Object[][] getJdbcTypeMapper(Template template) {
        return template != null ? template.getJdbcTypeMapper() : null;
    }

    /**
     * 编辑选择的模板组名称
     *
     * @param oldName 选择的模板组名称
     * @param newName 新名称
     */
    public void editGroupName(String oldName, String newName) {
        if (StringUtils.isNotBlank(newName)) {
            Template template = getTemplate(oldName);
            template.setGroupName(newName);
        }
    }

    /**
     * 编辑选择的模板文件名称
     *
     * @param groupName 所属模板组名称
     * @param oldName   原模板文件名称
     * @param newName   新名称
     */
    public void editGroupFileName(String groupName, String oldName, String newName) {
        if (StringUtils.isNotBlank(newName)) {
            Template template = getTemplate(groupName);
            List<String> groupFiles = template.getGroupFiles();
            // 修改模板文件列表里对应的名称信息
            if (groupFiles != null) {
                int fileIndex = groupFiles.indexOf(oldName);
                if (fileIndex >= 0) {
                    groupFiles.remove(fileIndex);
                    groupFiles.add(fileIndex, newName);
                }
            }

            // 修改模板文件映射key值名称
            Map<String, String> contentMap = template.getFileContentMap();
            if (contentMap != null && contentMap.containsKey(oldName)) {
                String content = contentMap.get(oldName);
                contentMap.remove(oldName);
                contentMap.put(newName, content);
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
        if (this.templates != null && StringUtils.isNotBlank(groupName)) {
            for (Template template : this.templates) {
                if (template.getGroupName().equals(groupName)) {
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
        tp.setApiDoc(template.getApiDoc());
        tp.setGlobalConfig(template.getGlobalConfig());
        // 复制原模板组配置的JavaType类型映射信息
        tp.setTypeMapper(template.getTypeMapper());
        // 复制原模板组配置的JdbcType类型映射信息
        tp.setJdbcTypeMapper(template.getJdbcTypeMapper());
        this.templates.add(tp);
    }

    /**
     * 复制模板文件
     *
     * @param copyGroupName 复制模板组名称
     * @param copyFileName  复制模板文件名称
     * @param rename        模板名称
     */
    public void copyGroupFile(String copyGroupName, String copyFileName, String rename) {
        if (this.templates != null) {
            for (int i = 0; i < this.templates.size(); i++) {
                Template template = this.templates.get(i);
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
        template.setTypeMapper(DefaultTemplateParams.getDefaultTableData());
        template.setJdbcTypeMapper(DefaultTemplateParams.getDefaultJdbcTypeTableData());
        this.templates.add(template);
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
        int index = this.templates.indexOf(template);
        commonOperate(template, rename, content, index);
    }

    /**
     * 删除模板组
     *
     * @param groupName 删除模板名称
     */
    public void delGroup(String groupName) {
        Template template = getTemplate(groupName);
        this.templates.remove(template);
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
        int index = this.templates.indexOf(template);
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
        this.templates.remove(index);
        // 根据删除模板对象下标，新增新的模板对象
        this.templates.add(index, t);
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
        this.templates.remove(index);
        // 添加新的
        this.templates.add(index, t);
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
            map.put(name, content);
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
     * @param template 模板对象
     * @param rename   新的模板文件名称
     * @return boolean
     */
    public boolean existGroupFile(Template template, String rename) {
        List<String> groupFiles = getGroupFileNames(template);
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
                        if (StringUtils.isNotBlank(param)) {
                            String key = param.substring(0, param.indexOf(":")).toUpperCase();
                            String value = param.substring(param.indexOf(":") + 1);
                            // 判断自定义数源导入文件类型是否符合规范
                            if (key.equals(TemplateJInterface.CUSTOM_DATASOURCE) && !isAllowFileType(value)) {
                                continue;
                            }
                            map.put(key, value);
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
     * 获取模板代码，移除模板文件中插件属性配置项
     *
     * @param templateCode 模板内容
     * @return string
     */
    public synchronized String getSourceCode(String templateCode, String globalConfig) {
        if (StringUtils.isNotBlank(templateCode) && templateCode.contains("\n")) {
            this.propUtil = new PropertiesUtil();
            setSettingProperties(templateCode, this.propUtil);
            String[] tcs = templateCode.split("\n");
            StringBuilder builder = new StringBuilder();
            for (String s : tcs) {
                s = s.trim();
                // 移除模板文件中，##开头的所有插件属性配置项内容
                if (!s.startsWith("##")) {
                    builder.append(s).append("\n");
                }
            }
            templateCode = builder.toString().trim();
            // 是否忽略全局配置
            boolean ignoreGlobal = Boolean.parseBoolean(this.propUtil.getValue(TemplateJInterface.IGNORE_GLOBAL));
            // 非全局配置忽略时，添加全局配置项内容
            if (!ignoreGlobal && StringUtils.isNotBlank(globalConfig)) {
                templateCode = globalConfig.concat(templateCode).trim();
            }
        }
        return templateCode;
    }

    /**
     * 自定义数据源文件允许的文件类型
     *
     * @param filePath 文件路径
     * @return Boolean true-允许类型，false-不允许类型
     */
    private static boolean isAllowFileType(String filePath) {
        return FileUtil.isFileType(filePath, ConfigInterface.CUSTOM_DATASOURCE_FILE_TYPE);
    }
}
