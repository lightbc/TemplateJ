package com.lightbc.templatej.listener;

import com.alibaba.fastjson.JSONArray;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.ImportUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板导入
 */
public class ImportListener {
    private TemplateJUI templateJUI;

    public ImportListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        importTemplate();
    }

    /**
     * 模板导入
     */
    private void importTemplate() {
        this.templateJUI.getImportButton().addActionListener((e) -> {
            DialogUtil dialogUtil = new DialogUtil();
            ImportUI importUI = new ImportUI(this.templateJUI);
            dialogUtil.setOkBtn(importUI.getOk());
            dialogUtil.setCancelBtn(importUI.getCancel());
            confirmListener(importUI, dialogUtil, dialogUtil.getOkBtn());
            dialogUtil.showCustomDialog(ConfigInterface.TEMPLATE_IMPORT, importUI.getMainPanel(), templateJUI.getMainPanel(), new Dimension(550, 400), false);
        });
    }

    /**
     * 操作确认监听
     *
     * @param ui     主UI界面对象
     * @param dialog 对话框对象
     * @param button 确认按钮
     */
    private void confirmListener(ImportUI ui, DialogUtil dialog, JButton button) {
        button.addActionListener(e -> {
            // 参数校验
            boolean b = checkItem(ui, dialog);
            if (b) {
                Template template = getImportTemplate(ui);
                TemplateJSettings settings = this.templateJUI.getSettings();
                // 判断是否存在同名的模板组
                boolean ge = groupExist(settings.getTemplates(), template);
                if (ge) {
                    dialog.showTipsDialog(ui.getMainPanel(), Message.IMPORT_GROUP_EXIST.getMsg(), Message.IMPORT_GROUP_EXIST.getTitle());
                    // 存在同名的模板组，将导入的模板组名称重新命名
                    String newGroupName = ui.getGroupName().getText().trim();
                    if (StringUtils.isNotBlank(newGroupName)) {
                        ui.getGroupName().setText(newGroupName.concat(ConfigInterface.DEFAULT_RENAME_SUFFIX));
                    }
                    return;
                }
                // 新增模板
                settings.getTemplates().add(template);
                // 设置导入模板组为默认选择模板组
                settings.setSelectGroup(template.getGroupName());
                // 设置默认选择的模板文件
                settings.setSelectGroupFile(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
                dialog.showTipsDialog(ui.getMainPanel(), Message.IMPORT_SUCCESS.getMsg(), Message.IMPORT_SUCCESS.getTitle());
                dialog.dispose();
            }
            // 刷新主UI面板
            this.templateJUI.refresh();
        });
    }

    /**
     * 判断模板组是否存在
     *
     * @param templates      已有模板
     * @param importTemplate 导入的模板
     * @return boolean true-存在，false-不存在
     */
    private boolean groupExist(List<Template> templates, Template importTemplate) {
        if (templates != null) {
            if (importTemplate != null) {
                for (Template template : templates) {
                    String haveGroupName = template.getGroupName();
                    String importGroupName = importTemplate.getGroupName();
                    if (haveGroupName.trim().equals(importGroupName.trim())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取导入模板对象
     *
     * @param ui 导入UI界面
     * @return template 模板
     */
    private Template getImportTemplate(ImportUI ui) {
        Template template = null;
        if (ui != null) {
            template = new Template();
            // 设置模板组名称
            String groupName = ui.getGroupName().getText();
            template.setGroupName(groupName);
            // 获取勾选的需要导入的模板项
            Map<String, String> itemMap = getCheckItems(ui.getImportFileContainer(), ui.getParentDir());
            if (itemMap != null && itemMap.size() > 0) {
                List<String> fileList = new ArrayList<>();
                Map<String, String> contentMap = new HashMap<>();
                for (String key : itemMap.keySet()) {
                    // 判断文件格式，移除*.tj格式文件
                    if (key.lastIndexOf(ConfigInterface.PLUGIN_DEFAULT_EXT) == -1) {
                        fileList.add(key);
                        contentMap.put(key, readContent(itemMap.get(key)));
                    }
                }
                // 全局配置
                String globalPath = ui.getGlobalConfigButton().getText();
                if (StringUtils.isNotBlank(globalPath)) {
                    String globalContent = readContent(globalPath);
                    template.setGlobalConfig(globalContent);
                }
                // API接口文档模板
                String apiDocPath = ui.getApiDocButton().getText();
                if (StringUtils.isNotBlank(apiDocPath)) {
                    String apiDocContent = readContent(apiDocPath);
                    template.setApiDoc(apiDocContent);
                }
                // 设置模板文件
                template.setGroupFiles(fileList);
                // 设置模板文件内容
                template.setFileContentMap(contentMap);
            }
            // 导入类型映射器默认数据
            getTypeMapper(ui, template);
        }
        return template;
    }

    /**
     * 读取文件内容
     *
     * @param path 文件路径
     * @return string 文件内容
     */
    private String readContent(String path) {
        FileUtil fileUtil = new FileUtil();
        return fileUtil.read(path);
    }

    /**
     * 参数校验
     *
     * @param ui     导入UI界面
     * @param dialog 对话框
     * @return boolean true-校验通过，false-校验未通过
     */
    private boolean checkItem(ImportUI ui, DialogUtil dialog) {
        // 导入模板路径选择验证（文件夹/文件）
        String importPath = ui.getBrowseButton().getText();
        if (StringUtils.isBlank(importPath)) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_SELECT_IMPORT_PATH.getMsg(), Message.NO_SELECT_IMPORT_PATH.getTitle());
            return false;
        }
        // 默认模板组名称验证
        String groupName = ui.getGroupName().getText();
        if (StringUtils.isBlank(groupName)) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_DEFAULT_GROUP_NAME.getMsg(), Message.NO_DEFAULT_GROUP_NAME.getTitle());
            return false;
        }
        // 导入模板文件选择验证
        Map<String, String> itemMap = getCheckItems(ui.getImportFileContainer(), ui.getParentDir());
        if (itemMap == null || itemMap.size() == 0) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_SELECT_IMPORT_TEMPLATE_FILE.getMsg(), Message.NO_SELECT_IMPORT_TEMPLATE_FILE.getTitle());
            return false;
        }
        return true;
    }

    /**
     * 获取勾选的模板文件项
     *
     * @param checkBoxParent 文件项复选框父级容器
     * @param parentDir      文件的上一级文件夹目录
     * @return map key-文件名，value-文件路径
     */
    private Map<String, String> getCheckItems(JComponent checkBoxParent, String parentDir) {
        Map<String, String> map = null;
        if (checkBoxParent != null) {
            // 获取容器中的所有组件
            Component[] components = checkBoxParent.getComponents();
            if (components != null && components.length > 0) {
                map = new HashMap<>();
                for (Component component : components) {
                    // 将容器组件转换成复选框组件对象
                    JCheckBox box = (JCheckBox) component;
                    // 获取被选中的复选框
                    if (box.isSelected()) {
                        String fileName = box.getText();
                        if (StringUtils.isNotBlank(parentDir)) {
                            String filePath = parentDir.concat(File.separator).concat(fileName);
                            map.put(fileName, filePath);
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 导入类型映射器配置数据内容
     *
     * @param ui       导入UI界面
     * @param template 导入的模板对象
     */
    private void getTypeMapper(ImportUI ui, Template template) {
        // 获取父级文件目录路径
        String parentDir = ui.getParentDir();
        if (StringUtils.isNotBlank(parentDir)) {
            // 判断JavaType复选框是否选择
            if (ui.getJavaType().isSelected()) {
                Object[][] tm = null;
                try {
                    // 读取JavaType类型映射配置数据
                    String filePath = parentDir.concat(File.separator).concat(ui.getJavaType().getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
                    String content = readContent(filePath);
                    if (StringUtils.isNotBlank(content)) {
                        tm = JSONArray.parseObject(content, Object[][].class);
                    }
                } catch (Exception ignore) {
                } finally {
                    // 读取的JavaType类型配置数据为空，重置为默认数据
                    if (tm != null) {
                        template.setTypeMapper(tm);
                    } else {
                        template.setTypeMapper(DefaultTemplateParams.getDefaultTableData());
                    }
                }
            }
            // 判断JdbcType复选框是否选择
            if (ui.getJdbcType().isSelected()) {
                Object[][] tm = null;
                try {
                    // 读取JdbcType类型映射配置数据
                    String filePath = parentDir.concat(File.separator).concat(ui.getJdbcType().getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
                    String content = readContent(filePath);
                    if (StringUtils.isNotBlank(content)) {
                        tm = JSONArray.parseObject(content, Object[][].class);
                    }
                } catch (Exception ignore) {
                } finally {
                    // 读取的JdbcType类型配置数据为空，重置为默认数据
                    if (tm != null) {
                        template.setJdbcTypeMapper(tm);
                    } else {
                        template.setJdbcTypeMapper(DefaultTemplateParams.getDefaultJdbcTypeTableData());
                    }
                }
            }
        }
    }

}
