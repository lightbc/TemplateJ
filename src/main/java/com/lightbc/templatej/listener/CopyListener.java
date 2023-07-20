package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 复制模板事件监听
 */
@Slf4j
public class CopyListener {
    // 配置界面UI
    private TemplateJUI templateJUI;
    private TemplateJCommonUI commonUI;

    public CopyListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void loadCommonUI() {
        this.commonUI = new TemplateJCommonUI(this.templateJUI.getSettings(), this.templateJUI.getTemplateUtil());
    }

    private void init() {
        loadCommonUI();
        copy();
    }

    /**
     * 复制模板组/模板文件监听功能
     */

    public void copy() {
        this.templateJUI.getCopy().addActionListener(e -> {
            loadCommonUI();
            this.commonUI.disable();
            this.commonUI.showCopy();
            //新名称输入框，填入初始化值
            String groupFileName = this.commonUI.getGroupFileName();
            if (TemplateUtil.isTemplateFile(groupFileName)) {
                this.commonUI.getRename().setText(CommonUtil.getDefaultCopyName(groupFileName));
            } else {
                this.commonUI.getRename().setText(CommonUtil.getDefaultCopyName(this.commonUI.getGroupName()));
            }
            //复制操作对话框
            int c = copy(commonUI.getMainPanel());
            //确认复制操作
            if (c == 0) {
                String groupName = this.commonUI.getGroupName();
                String fileName = this.commonUI.getGroupFileName();
                String rename = this.commonUI.getRename().getText();
                int fileIndex = this.commonUI.getTemplateFileSelector().getSelectedIndex();
                //复制
                TemplateUtil templateUtil = this.templateJUI.getTemplateUtil();
                Template template = templateUtil.getTemplate(groupName);
                DialogUtil dialogUtil = new DialogUtil();
                // 模板组
                if (fileIndex == 0) {
                    if (templateUtil.existGroup(rename)) {
                        dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                        return;
                    }
                    // 复制模板组
                    templateUtil.copyGroup(rename, template);
                    this.templateJUI.getSettings().setSelectGroup(rename);
                    this.templateJUI.getSettings().setSelectGroupFile(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
                }
                // 模板文件
                if (fileIndex > 0) {
                    Template temp = templateUtil.getTemplate(groupName);
                    if (templateUtil.existGroupFile(temp, rename)) {
                        dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                        return;
                    }
                    // 复制模板文件
                    templateUtil.copyGroupFile(groupName, fileName, rename);
                    this.templateJUI.getSettings().setSelectGroup(groupName);
                    this.templateJUI.getSettings().setSelectGroupFile(rename);
                }
                // 刷新主UI界面
                this.templateJUI.refresh();
            }
        });
    }

    /**
     * 复制模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    private int copy(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(this.templateJUI.getMainPanel(), message, Message.COPY_TEMPLATE.getTitle());
    }
}
