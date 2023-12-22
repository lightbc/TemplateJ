package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.TemplateJInterface;
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
                Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
                String templateCode = this.templateJUI.getTemplateUtil().getTemplateContent(template, templateFileName);
                String globalConfig = template.getGlobalConfig();
                // 模板文件内容为空提示
                if (templateCode == null || "".equals(templateCode.trim())) {
                    dialogUtil.showTipsDialog(null, Message.TEMPLATE_CONTENT_EMPTY.getMsg(), Message.TEMPLATE_CONTENT_EMPTY.getTitle());
                    return;
                }
                // 获取完整模板信息（全局配置+单个模板）
                PropertiesUtil util = new PropertiesUtil();
                String sourceCode = TemplateUtil.getSourceCode(templateCode, util);
                // 是否忽略全局配置
                boolean ignoreGlobal = Boolean.parseBoolean(util.getValue(TemplateJInterface.IGNORE_GLOBAL));
                // // 非全局配置忽略时，全局配置信息为空校验
                if (!ignoreGlobal && "".equals(globalConfig.trim())) {
                    dialogUtil.showTipsDialog(null, Message.GLOBAL_CONFIG_EMPTY.getMsg(), Message.GLOBAL_CONFIG_EMPTY.getTitle());
                    return;
                }
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
        // 自定义数源文件路径
        String customDataSourcePath = util.getValue(TemplateJInterface.CUSTOM_DATASOURCE);
        PreviewUI previewUI = new PreviewUI(groupName, templateFileName, sourceCode, dataBaseUtil, customDataSourcePath, auto);
        dialogUtil.showPreviewDialog(Message.PREVIEW_TEMPLATE.getTitle(), previewUI.getMainPanel(), previewUI.getEditorUtil());
    }
}
