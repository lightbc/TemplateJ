package com.lightbc.templatej.listener;

import com.alibaba.fastjson.JSONArray;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.ExportUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.CommonUtil;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import com.lightbc.templatej.utils.TemplateUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 模板导出按钮监听
 */
public class ExportListener {
    private TemplateJUI templateJUI;
    private TemplateUtil templateUtil;

    public ExportListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        exportTemplate();
    }

    /**
     * 模板导出功能
     */
    private void exportTemplate() {
        this.templateJUI.getExportButton().addActionListener((e) -> showExport());
    }

    /**
     * 显示导出UI界面
     */
    private void showExport() {
        this.templateUtil = new TemplateUtil(TemplateJSettings.getInstance().getTemplates());
        DialogUtil dialogUtil = new DialogUtil();
        ExportUI exportUI = new ExportUI(this.templateJUI);
        dialogUtil.setOkBtn(exportUI.getOk());
        dialogUtil.setCancelBtn(exportUI.getCancel());
        confirmListener(exportUI, dialogUtil, dialogUtil.getOkBtn());
        dialogUtil.showCustomDialog(ConfigInterface.TEMPLATE_EXPORT, exportUI.getMainPanel(), templateJUI.getMainPanel(), new Dimension(550, 400), false);
    }

    /**
     * 操作确认监听
     *
     * @param ui     导出UI界面
     * @param dialog 对话框
     * @param button 确认按钮
     */
    private void confirmListener(ExportUI ui, DialogUtil dialog, JButton button) {
        button.addActionListener(e -> {
            boolean b = checkItem(ui, dialog);
            if (b) {
                FileUtil fileUtil = new FileUtil();
                // 选择的导出位置
                String selectExportPath = ui.getBrowseButton().getText();
                // 选择的模板组名称
                String groupName = Objects.requireNonNull(ui.getTemplateGroup().getSelectedItem()).toString();
                // 导出的文件夹目录
                String dirPath = selectExportPath.concat(File.separator).concat(groupName);
                fileUtil.createDirs(dirPath);
                // 获取需要导出的模板文件项
                List<String> templateFiles = getCheckItems(ui.getExportFileContainer());
                if (templateFiles != null && templateFiles.size() > 0) {
                    for (String templateFile : templateFiles) {
                        // 文件导出路径，单个模板导出
                        String filePath = dirPath.concat(File.separator).concat(templateFile);
                        fileUtil.createFile(filePath);
                        fileUtil.write(filePath, getTemplateContent(groupName, templateFile));
                    }
                }
                Template template = this.templateUtil.getTemplate(groupName);
                // 导出选择的JavaType/JdbcType类型映射配置数据
                if (ui.getJavaType().isSelected()) {
                    String filePath = dirPath.concat(File.separator).concat(ui.getJavaType().getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
                    String javaType = JSONArray.toJSONString(template.getTypeMapper());
                    fileUtil.write(filePath, javaType);
                }
                if (ui.getJdbcType().isSelected()) {
                    String filePath = dirPath.concat(File.separator).concat(ui.getJdbcType().getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
                    String jdbcType = JSONArray.toJSONString(template.getJdbcTypeMapper());
                    fileUtil.write(filePath, jdbcType);
                }
                // 导出全局配置文件
                if (ui.getGlobalBox() != null && ui.getGlobalBox().isSelected()) {
                    String globalConfig = template.getGlobalConfig();
                    if (StringUtils.isNotBlank(globalConfig)) {
                        String filePath = dirPath.concat(File.separator).concat(ui.getGlobalBox().getText());
                        fileUtil.createFile(filePath);
                        fileUtil.write(filePath, globalConfig);
                    }
                }
                // 导出API接口文档配置
                if (ui.getApiDoc() != null && ui.getApiDoc().isSelected()) {
                    String apiDoc = template.getApiDoc();
                    if (StringUtils.isNotBlank(apiDoc)) {
                        String filePath = dirPath.concat(File.separator).concat(CommonUtil.getUUID().concat(ConfigInterface.API_DOC_EXT));
                        fileUtil.createFile(filePath);
                        fileUtil.write(filePath, apiDoc);
                    }
                }
                dialog.showTipsDialog(ui.getMainPanel(), Message.EXPORT_SUCCESS.getMsg(), Message.EXPORT_SUCCESS.getTitle());
                dialog.dispose();
            }
        });
    }

    /**
     * 获取导出模板内容
     *
     * @param groupName    模板组名称
     * @param templateName 模板文件名称
     * @return string 模板文件内容
     */
    private String getTemplateContent(String groupName, String templateName) {
        Template template = this.templateUtil.getTemplate(groupName);
        return this.templateUtil.getTemplateContent(template, templateName);
    }

    /**
     * 参数校验
     *
     * @param ui     导出UI界面
     * @param dialog 对话框
     * @return boolean true-校验通过，false-校验不通过
     */
    private boolean checkItem(ExportUI ui, DialogUtil dialog) {
        // 导出模板组选择校验
        Object selectGroup = ui.getTemplateGroup().getSelectedItem();
        if (selectGroup == null) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_SELECT_GROUP.getMsg(), Message.NO_SELECT_GROUP.getTitle());
            return false;
        }
        // 导出模板文件选择校验
        List<String> items = getCheckItems(ui.getExportFileContainer());
        if (items == null || items.size() == 0) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_SELECT_TEMPLATE_FILE.getMsg(), Message.NO_SELECT_TEMPLATE_FILE.getTitle());
            return false;
        }
        // 导出位置选择校验
        if (StringUtils.isBlank(ui.getBrowseButton().getText())) {
            dialog.showTipsDialog(ui.getMainPanel(), Message.NO_SELECT_EXPORT_PATH.getMsg(), Message.NO_SELECT_EXPORT_PATH.getTitle());
            return false;
        }
        return true;
    }

    /**
     * 获取选择的生成模板项
     *
     * @param checkBoxParent 选取按钮的父级容器
     * @return List<String> 选择的生成模板项数组
     */
    private List<String> getCheckItems(JComponent checkBoxParent) {
        // 获取容器中的所有组件
        Component[] components = checkBoxParent.getComponents();
        List<String> list = null;
        if (components != null && components.length > 0) {
            list = new ArrayList<>();
            for (Component component : components) {
                // 将容器组件转换成复选框组件对象
                JCheckBox box = (JCheckBox) component;
                // 获取被选中的复选框
                if (box.isSelected()) {
                    list.add(box.getText());
                }
            }
        }
        return list;
    }
}

