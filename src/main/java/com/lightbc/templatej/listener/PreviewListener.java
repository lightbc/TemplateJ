package com.lightbc.templatej.listener;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.TemplateJInterface;
import com.lightbc.templatej.ui.PreviewUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 预览事件监听
 */
@Slf4j
public class PreviewListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public PreviewListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 效果预览功能监听
     */

    public void preview() {
        templateJUI.getPreview().addActionListener(e -> {
            int index = templateJUI.getTemplateFileSelector().getSelectedIndex();
            DialogUtil dialogUtil = new DialogUtil();
            // 已选取预览文件
            if (index > 0) {
                String groupName = Objects.requireNonNull(templateJUI.getTemplateGroupSelector().getSelectedItem()).toString();
                String templateFileName = Objects.requireNonNull(templateJUI.getTemplateFileSelector().getSelectedItem()).toString();
                String templateCode = templateJUI.getTemplateUtil().getTemplateContent(groupName, templateFileName);
                String globalConfig = templateJUI.getTemplateUtil().getGlobalConfig(groupName);
                // 模板文件内容为空提示
                if (templateCode == null || "".equals(templateCode.trim())) {
                    dialogUtil.showTipsDialog(null, Message.TEMPLATE_CONTENT_EMPTY.getMsg(), Message.TEMPLATE_CONTENT_EMPTY.getTitle());
                    return;
                }
                // 全局配置信息为空提示
                if (globalConfig == null || "".equals(globalConfig.trim())) {
                    dialogUtil.showTipsDialog(null, Message.GLOBAL_CONFIG_EMPTY.getMsg(), Message.GLOBAL_CONFIG_EMPTY.getTitle());
                    return;
                }
                // 获取完整模板信息（全局配置+单个模板）
                PropertiesUtil util = new PropertiesUtil();
                String sourceCode = TemplateUtil.getSourceCode(globalConfig, templateCode, util);
                // 预览
                preview(groupName, templateFileName, sourceCode, util);
            } else {
                dialogUtil.showTipsDialog(null, Message.PREVIEW_CHOICE_TIP.getMsg(), Message.PREVIEW_CHOICE_TIP.getTitle());
            }
        });
    }

    /**
     * 效果预览
     *
     * @param groupName        模板组名称
     * @param templateFileName 模板文件名称
     * @param sourceCode       模板内容
     * @param util             插件属性工具
     */
    private void preview(String groupName, String templateFileName, String sourceCode, PropertiesUtil util) {
        DialogUtil dialogUtil = new DialogUtil();
        // 获取database已连接数据库连接信息
        DataBaseUtil dataBaseUtil = new DataBaseUtil();
        List<String> dbNames = dataBaseUtil.getDataSourceNames();
        if (dbNames == null || dbNames.size() == 0) {
            dialogUtil.showTipsDialog(null, Message.DATABASE_NOT_CONNECT.getMsg(), Message.DATABASE_NOT_CONNECT.getTitle());
            return;
        }
        // 显示效果预览组件
        boolean auto = Boolean.parseBoolean(util.getValue(TemplateJInterface.AUTO_PREVIEW));
        PreviewUI previewUI = new PreviewUI(groupName, templateFileName, sourceCode, dataBaseUtil, auto);
        dialogUtil.showPreviewDialog(Message.PREVIEW_TEMPLATE.getTitle(), previewUI.getMainPanel(), previewUI.getEditorUtil());
    }
}
