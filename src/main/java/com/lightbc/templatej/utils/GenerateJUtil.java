package com.lightbc.templatej.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.JBIterable;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.ColumnInfo;
import com.lightbc.templatej.entity.Generate;
import com.lightbc.templatej.entity.Table;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.interfaces.TemplateJInterface;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成处理工具类
 */
@Slf4j
public class GenerateJUtil {
    private FileUtil fileUtil = new FileUtil();
    // 字符编码
    @Getter
    private String encode = ConfigInterface.ENCODE_VALUE;
    private TemplateUtil templateUtil;
    // 是否关闭提示消息，true-关闭，false-不关闭
    @Setter
    private boolean closeTips;

    public GenerateJUtil() {
        // 获取模板信息
        this.templateUtil = new TemplateUtil(TemplateJSettings.getInstance().getTemplates());
    }

    /**
     * 根据模板生成文件
     *
     * @param groupName        模板组名称
     * @param templateFileName 模板文件名称
     * @param saveFileName     保存文件名称
     * @param savePath         保存路径
     * @param dataModel        模板引擎的数据模型
     */
    public synchronized void generate(String groupName, String templateFileName, String saveFileName, String savePath, Map<String, Object> dataModel) {
        DialogUtil dialogUtil = new DialogUtil();
        String ext = ConfigInterface.DEFAULT_GENERATE_EXT;
        if (saveFileName.contains(".")) {
            ext = saveFileName.substring(saveFileName.indexOf("."));
        }
        com.lightbc.templatej.entity.Template template = this.templateUtil.getTemplate(groupName);
        String templateCode = this.templateUtil.getTemplateContent(template, templateFileName);
        String globalConfig = template.getGlobalConfig();
        // 获取完整模板信息（全局配置+单个模板）
        String sourceCode = this.templateUtil.getSourceCode(templateCode, globalConfig);
        // 生成字符串内容
        String generateContent = generate(templateFileName, sourceCode, dataModel, this.templateUtil.getPropUtil());
        //template根据模板生成指定内容后，获取template处理后的数据模型，用于文件生成后续步骤
        if (dataModel.containsKey(ConfigInterface.GENERATE_KEY_NAME)) {
            Generate generate = ((Generate) dataModel.get(ConfigInterface.GENERATE_KEY_NAME));
            String customPath = savePath;
            // xml文件保存位置
            if (StringUtils.isNotBlank(generate.getXmlSavePath())) {
                customPath = generate.getXmlSavePath();
            } else if (StringUtils.isNotBlank(generate.getSavePath())) {
                String generatePath = generate.getSavePath();
                if (generatePath.substring(0, 1).equals(File.separator)) {
                    generatePath = generatePath.substring(1);
                }
                customPath = customPath.concat(File.separator).concat(generatePath);
            }
            if (!fileUtil.exist(customPath)) {
                fileUtil.createDirs(customPath);
            }
            if (StringUtils.isNotBlank(generate.getFileName())) {
                saveFileName = generate.getFileName();
                //自定义文件名中如果不包含指定文件拓展名，使用默认文件拓展名
                if (!saveFileName.contains(".")) saveFileName = saveFileName.concat(ext);
                savePath = customPath.concat(File.separator).concat(saveFileName);
            }
        } else {
            savePath = savePath.concat(File.separator).concat(saveFileName);
        }
        // 提示消息
        if (!this.closeTips) {
            boolean b = this.fileUtil.exist(savePath);
            if (b) {
                int c = dialogUtil.showConfirmDialog(null, String.format(Message.TEMPLATE_REPEAT_CREATE.getMsg(), saveFileName), Message.TEMPLATE_REPEAT_CREATE.getTitle());
                if (c != 0) {
                    return;
                }
            }
        }
        // 生成文件
        fileUtil.createFile(savePath);
        fileUtil.write(savePath, generateContent);
        // 刷新工程目录结构
        VirtualFileManager.getInstance().syncRefresh();
    }

    /**
     * 生成字符串内容
     *
     * @param templateFileName 模板名称
     * @param sourceCode       模板内容
     * @param dataModel        模板模型数据
     * @param propUtil         插件自定义属性对象
     * @return 生成内容字符串
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public synchronized String generate(String templateFileName, String sourceCode, Map<String, Object> dataModel, PropertiesUtil propUtil) {
        StringWriter writer = null;
        try {
            addCustomDataSourceModel(dataModel, propUtil);
            Template template = getTemplate(templateFileName, sourceCode);
            writer = new StringWriter();
            template.process(dataModel, writer);
            writer.flush();
        } catch (Exception e) {
            writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            log.error("模板生成错误：{}-{}", e.getMessage(), e.getCause());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                log.error("模板生成输出流关闭错误：{}-{}", e.getMessage(), e.getCause());
            }
            assert writer != null;
            return writer.toString().trim();
        }
    }

    /**
     * 获取模板引擎配置信信息
     *
     * @return 模板引擎配置项
     */
    private Configuration getConfiguration() {
        Configuration cfg = new Configuration(Configuration.getVersion());
        StringTemplateLoader loader = new StringTemplateLoader();
        cfg.setTemplateLoader(loader);
        cfg.setDefaultEncoding(encode);
        return cfg;
    }

    /**
     * 获取模板引擎模板
     *
     * @param fileName   模板名称
     * @param sourceCode 模板内容
     * @return 模板
     * @throws Exception
     */
    @SuppressWarnings("JavaDoc")
    private Template getTemplate(String fileName, String sourceCode) throws Exception {
        return new Template(fileName, sourceCode, getConfiguration());
    }

    /**
     * 获取数据表的主要信息
     *
     * @param groupName 模板组名称
     * @param dt        数据表
     * @return Table
     */
    private Table getTable(String groupName, DbTable dt) {
        Table table = new Table();
        // 获取当前表的所在数据库名称
        assert dt.getParent() != null;
        table.setSchemaName(dt.getParent().getName());
        // 获取表名
        table.setOriginName(dt.getName());
        // 获取处理过后的表名，大驼峰格式
        String tableName = processName(dt.getName());
        table.setName(tableName);
        // 获取表备注信息
        table.setComment(dt.getComment());

        // 获取表数据列
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dt);
        List<ColumnInfo> columnsList = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        Map<String, Object> columnType = new HashMap<>();
        // 获取javaType类型映射关系
        Map<String, String> javaTypeMapper = javaTypeMapper(groupName);
        // 获取jdbcType类型映射关系
        Map<String, String> jdbcTypeMapper = jdbcTypeMapper(groupName);
        for (DasColumn column : columns) {
            //处理主键
            if (DasUtil.isPrimary(column)) {
                table.setOriginPrimaryKey(column.getName());
                table.setPrimaryKey(processName(column.getName()));
            }
            ColumnInfo cols = new ColumnInfo();
            // 获取列名
            cols.setOriginColumnName(column.getName());
            // 获取处理过后的列名，大驼峰格式
            String columnName = processName(column.getName());
            cols.setColumnName(columnName);
            // 数据类型
            String dataType = column.getDataType().typeName;
            cols.setDataType(dataType);
            // 字段存储长度
            int size = column.getDataType().size;
            cols.setSize(size);
            // 获取对应的javaType数据类型
            cols.setJavaType(javaTypeMapper.get(dataType));
            // 获取对应的jdbcType数据类型
            cols.setJdbcType(jdbcTypeMapper.get(dataType));
            // 列备注信息
            cols.setColumnComment(column.getComment());
            columnsList.add(cols);
            columnNames.add(processName(column.getName()));
            columnType.put(columnName, javaTypeMapper.get(dataType));
        }
        table.setColumns(columnsList);
        table.setColumnNames(columnNames);
        table.setColumnType(columnType);
        table.setDbTable(dt);
        return table;
    }

    /**
     * 获取预览功能的默认模板引擎数据模型
     *
     * @param groupName 模板组名称
     * @param table     选择的数据表
     * @return Map<String, Object>
     */
    public Map<String, Object> getPreviewDataModel(String groupName, DbTable table) {
        Table t = getTable(groupName, table);
        Project project = ProjectUtil.getProject();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        String root;
        root = ProjectUtil.getModulePath(modules[0]);
        return getCommonDataModel(root, ConfigInterface.DEFAULT_PACKAGE_NAME, null, new Generate(), t);
    }

    /**
     * 获取模板引擎的数据模型
     *
     * @param groupName    模板组名称
     * @param table        数据表
     * @param fileName     模板文件名称
     * @param rootPath     项目根路径
     * @param packagePath  包路径
     * @param generatePath 生成路径
     * @return Map<String, Object>
     */
    public Map<String, Object> getDataModel(String groupName, DbTable table, String fileName, String rootPath, String packagePath, String generatePath) {
        Table t = getTable(groupName, table);
        Generate generate = new Generate();
        if (StringUtils.isNotBlank(fileName)) {
            fileName = t.getName().concat(fileName.substring(fileName.indexOf(".")));
        }
        generate.setFileName(fileName);
        return getCommonDataModel(rootPath, packagePath, generatePath, generate, t);
    }

    /**
     * 获取通用的模板引擎的数据模型
     *
     * @param root         项目根路径
     * @param packageName  包名
     * @param generatePath 生成路径
     * @param generate     生成时的自定义信息对象
     * @param t            数据表处理后对象
     * @return Map<String, Object>
     */
    public Map<String, Object> getCommonDataModel(String root, String packageName, String generatePath, Generate generate, Table t) {
        Map<String, Object> map = new HashMap<>();
        ToolsUtil toolsUtil = new ToolsUtil();
        map.put("rootPath", root);
        map.put("tools", toolsUtil);
        map.put("packageName", packageName);
        map.put("table", t);
        map.put("tableName", t != null ? t.getName() : null);
        map.put("generatePath", generatePath);
        map.put(ConfigInterface.GENERATE_KEY_NAME, generate);
        return map;
    }

    /**
     * 获取完整的保存路径信息
     *
     * @param path   原路径信息
     * @param custom 自定义路径部分
     * @return string
     */
    public String getCompleteSavePath(String path, String custom) {
        if (custom != null && !"".equals(custom.trim())) {
            //自适应自定义路径，开始出现路径分隔符问题
            if (File.separator.equals(custom.substring(0, 1))) {
                path += custom;
            } else {
                path += File.separator.concat(custom);
            }
        }
        return path;
    }

    /**
     * 名称大驼峰格式处理
     *
     * @param name 处理字符
     * @return string
     */
    private String processName(String name) {
        if (name != null && !"".equals(name.trim())) {
            // 处理下划线
            if (name.contains("_")) {
                String[] ns = name.split("_");
                StringBuilder nns = new StringBuilder();
                for (String n : ns) {
                    if (!"".equals(n.trim())) {
                        nns.append(ToolsUtil.getInstance().toUpperCamelCase(n));
                    }
                }
                name = nns.toString();
            } else {
                name = ToolsUtil.getInstance().toUpperCamelCase(name);
            }
        }
        return name;
    }

    /**
     * 数据类型映射器
     *
     * @param groupName 模板组名称
     * @param type      映射器类型，0：JavaType类型映射器，1：JdbcType类型映射器
     * @return Map<String, String>
     */
    private Map<String, String> typeMapper(String groupName, int type) {
        Map<String, String> map = new HashMap<>();
        com.lightbc.templatej.entity.Template template = this.templateUtil.getTemplate(groupName);
        Object[][] typeMapper = null;
        if (type == 0) {
            typeMapper = this.templateUtil.getTypeMapper(template);
            if (typeMapper == null) {
                typeMapper = DefaultTemplateParams.getDefaultTableData();
            }
        } else if (type == 1) {
            typeMapper = this.templateUtil.getJdbcTypeMapper(template);
            if (typeMapper == null) {
                typeMapper = DefaultTemplateParams.getDefaultJdbcTypeTableData();
            }
        }
        if (groupName != null && !"".equals(groupName.trim())) {
            if (typeMapper != null) {
                for (Object[] obj : typeMapper) {
                    // column type
                    String s0 = obj[0].toString();
                    // java type/ jdbc type
                    String s1 = processTypeMapper(obj[1].toString());
                    map.put(s0, s1);
                }
            }
        }
        return map;
    }

    /**
     * JavaType数据类型映射器
     *
     * @param groupName 模板组名称
     * @return Map<String, String>
     */
    private Map<String, String> javaTypeMapper(String groupName) {
        return typeMapper(groupName, 0);
    }

    /**
     * JdbcType数据类型映射器
     *
     * @param groupName 模板组名称
     * @return Map<String, String>
     */
    private Map<String, String> jdbcTypeMapper(String groupName) {
        return typeMapper(groupName, 1);
    }

    /**
     * 数据类型映射器 java type 处理
     *
     * @param s java type
     * @return string
     */
    private String processTypeMapper(String s) {
        assert s != null;
        if (!"".equals(s.trim())) {
            if (s.lastIndexOf(".") != -1) {
                String name = s.substring(s.lastIndexOf(".") + 1);
                name = StringUtils.capitalize(name);
                return name;
            }
            return StringUtils.capitalize(s);
        }
        return s;
    }

    /**
     * 添加自定义数据源到freemarker数据模型中
     *
     * @param dataModel freemarker数据模型
     * @param propUtil  插件自定义属性对象
     */
    private synchronized void addCustomDataSourceModel(Map<String, Object> dataModel, PropertiesUtil propUtil) {
        if (dataModel == null || propUtil == null) {
            return;
        }
        DialogUtil dialog = new DialogUtil();
        try {
            String sourcePath = propUtil.getValue(TemplateJInterface.CUSTOM_DATASOURCE);
            if (StringUtils.isNotBlank(sourcePath)) {
                FileUtil fileUtil = new FileUtil();
                String res = fileUtil.read(sourcePath);
                // 自定义数源内容/格式是否正确，true-不正确，false-正确
                boolean isEmptyCustomDataSource = false;
                // 自定义数源导入
                if (StringUtils.isNotBlank(res)) {
                    if (CommonUtil.isJsonObject(res)) {
                        // 判断数据类型是json对象还是json数组
                        JSONObject json = JSONObject.parseObject(res);
                        if (json != null && json.size() > 0) {
                            dataModel.put("customData", json);
                        }
                    } else if (CommonUtil.isJsonArray(res)) {
                        JSONArray array = JSONArray.parseArray(res);
                        if (array != null && array.size() > 0) {
                            dataModel.put("customData", array);
                        }
                    } else {
                        isEmptyCustomDataSource = true;
                    }
                } else {
                    isEmptyCustomDataSource = true;
                }
                if (!this.closeTips && isEmptyCustomDataSource) {
                    dialog.showTipsDialog(null, Message.CUSTOM_DATASOURCE_EMPTY.getMsg(), Message.CUSTOM_DATASOURCE_EMPTY.getTitle());
                }
            }
        } catch (Exception ignore) {
            if (!this.closeTips) {
                dialog.showTipsDialog(null, Message.ADD_CUSTOM_DATASOURCE_ERROR.getMsg(), Message.ADD_CUSTOM_DATASOURCE_ERROR.getTitle());
            }
        }
    }

}
