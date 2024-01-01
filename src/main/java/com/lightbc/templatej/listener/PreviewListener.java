package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.ui.PreviewUI;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 预览事件监听
 */
@Slf4j
public class PreviewListener {
    // 配置界面UI
    private TemplateJUI templateJUI;
    private TemplateJCommonUI commonUI;

    public PreviewListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        this.commonUI = templateJUI.getCommonUI();
        preview();
    }

    /**
     * 效果预览功能监听
     */
    public void preview() {
        this.templateJUI.getPreview().addActionListener(e -> {
            DialogUtil dialogUtil = new DialogUtil();
            // 已选取预览文件
            if (TemplateUtil.isTemplateFile(this.commonUI.getGroupFileName())) {
                String groupName = this.commonUI.getGroupName();
                String templateFileName = this.commonUI.getGroupFileName();
                TemplateUtil templateUtil = this.templateJUI.getTemplateUtil();
                Template template = templateUtil.getTemplate(groupName);
                String templateCode = templateUtil.getTemplateContent(template, templateFileName);
                String globalConfig = template.getGlobalConfig();
                // 模板文件内容为空提示
                if (templateCode == null || "".equals(templateCode.trim())) {
                    dialogUtil.showTipsDialog(null, Message.TEMPLATE_CONTENT_EMPTY.getMsg(), Message.TEMPLATE_CONTENT_EMPTY.getTitle());
                    return;
                }
                // 获取完整模板信息（全局配置+单个模板）
                String sourceCode = templateUtil.getSourceCode(templateCode, globalConfig);
                // 预览
                doPreview(groupName, templateFileName, sourceCode, templateUtil.getPropUtil());
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
    private void doPreview(String groupName, String templateFileName, String sourceCode, PropertiesUtil util) {
        DialogUtil dialogUtil = new DialogUtil();
        // 获取database已连接数据库连接信息
        DataBaseUtil dataBaseUtil = new DataBaseUtil();
        List<String> dbNames = dataBaseUtil.getDataSourceNames();
        if (dbNames == null || dbNames.size() == 0) {
            dialogUtil.showTipsDialog(null, Message.DATABASE_NOT_CONNECT.getMsg(), Message.DATABASE_NOT_CONNECT.getTitle());
            return;
        }
        PreviewUI previewUI = new PreviewUI(groupName, templateFileName, sourceCode, dataBaseUtil, util);
        dialogUtil.showPreviewDialog(Message.PREVIEW_TEMPLATE.getTitle(), previewUI.getMainPanel(), previewUI.getEditorUtil());
    }
}
