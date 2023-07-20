package com.lightbc.templatej.utils;

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
import com.lightbc.templatej.ui.TemplateJGenerateUI;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成工具类
 */
@Slf4j
public class GenerateJUtil {
    private FileUtil fileUtil = new FileUtil();
    // 字符编码
    @Getter
    private String encode = ConfigInterface.ENCODE_VALUE;
    private TemplateUtil templateUtil;

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
     * @param tips             是否关闭提示消息，true-关闭，false-不关闭
     */
    public void generate(String groupName, String templateFileName, String saveFileName, String savePath, Map<String, Object> dataModel, boolean tips) {
        DialogUtil dialogUtil = new DialogUtil();
        String ext = ConfigInterface.DEFAULT_GENERATE_EXT;
        if (saveFileName.contains(".")) {
            ext = saveFileName.substring(saveFileName.indexOf("."));
        }
        com.lightbc.templatej.entity.Template template = this.templateUtil.getTemplate(groupName);
        String templateCode = this.templateUtil.getTemplateContent(template, templateFileName);
        String globalConfig = this.templateUtil.getGlobalConfig(template);
        String sourceCode = TemplateUtil.getSourceCode(globalConfig, templateCode, new PropertiesUtil());
        // 生成字符串内容
        String generateContent = generate(templateFileName, sourceCode, dataModel);
        //template根据模板生成指定内容后，获取template处理后的数据模型，用于文件生成后续步骤
        if (dataModel.containsKey("generate")) {
            Generate generate = ((Generate) dataModel.get("generate"));
            String customPath = savePath;
            // xml文件保存位置
            if (generate.getXmlSavePath() != null && !"".equals(generate.getXmlSavePath().trim())) {
                customPath = generate.getXmlSavePath();
            } else if (generate.getSavePath() != null && !"".equals(generate.getSavePath().trim())) {
                String generatePath = generate.getSavePath();
                if (generatePath.substring(0, 1).equals(File.separator)) {
                    generatePath = generatePath.substring(1);
                }
                customPath = customPath.concat(File.separator).concat(generatePath);
            }
            if (!fileUtil.exist(customPath)) {
                fileUtil.createDirs(customPath);
            }
            if (generate.getFileName() != null && !"".equals(generate.getFileName().trim())) {
                saveFileName = generate.getFileName();
                //自定义文件名中如果不包含指定文件拓展名，使用默认文件拓展名
                if (!saveFileName.contains(".")) saveFileName = saveFileName.concat(ext);
                savePath = customPath.concat(File.separator).concat(saveFileName);
            }
        } else {
            savePath = savePath.concat(File.separator).concat(saveFileName);
        }
        // 提示消息
        if (!tips) {
            boolean b = fileUtil.exist(savePath);
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
     * @return 生成内容字符串
     */
    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public String generate(String templateFileName, String sourceCode, Map<String, Object> dataModel) {
        StringWriter writer = null;
        try {
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
        // 获取该模板组的数据类型映射信息
        Map<String, String> typeMapper = typeMapper(groupName);
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
            // 获取对应的java数据类型
            cols.setJavaType(typeMapper.get(dataType));
            // 列备注信息
            cols.setColumnComment(column.getComment());
            columnsList.add(cols);
            columnNames.add(processName(column.getName()));
            columnType.put(columnName, typeMapper.get(dataType));
        }
        table.setColumns(columnsList);
        table.setColumnNames(columnNames);
        table.setColumnType(columnType);
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
     * @param generateUI   生成UI界面对象
     * @param generatePath 生成路径
     * @return Map<String, Object>
     */
    public Map<String, Object> getDataModel(String groupName, DbTable table, String fileName, TemplateJGenerateUI generateUI, String generatePath) {
        Table t = getTable(groupName, table);
        String root = ProjectUtil.getModulePath(generateUI.getSelectModule());
        String packageName = generateUI.getPackagePath().getText();
        Generate generate = new Generate();
        fileName = t.getName().concat(fileName.substring(fileName.indexOf(".")));
        generate.setFileName(fileName);
        return getCommonDataModel(root, packageName, generatePath, generate, t);
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
        map.put("generate", generate);
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
     * @return Map<String, String>
     */
    private Map<String, String> typeMapper(String groupName) {
        Map<String, String> map = new HashMap<>();
        com.lightbc.templatej.entity.Template template = this.templateUtil.getTemplate(groupName);
        Object[][] typeMapper = this.templateUtil.getTypeMapper(template);
        if (typeMapper == null) {
            typeMapper = DefaultTemplateParams.getDefaultTableData();
        }
        if (groupName != null && !"".equals(groupName.trim())) {
            if (typeMapper != null) {
                for (Object[] obj : typeMapper) {
                    // column type
                    String s0 = obj[0].toString();
                    // java type
                    String s1 = processTypeMapper(obj[1].toString());
                    map.put(s0, s1);
                }
            }
        }
        return map;
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

}
