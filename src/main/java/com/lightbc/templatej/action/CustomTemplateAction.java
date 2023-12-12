package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.ui.CustomTemplateExportUI;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 自定义模板（非数据接口类模板）操作action
 */
public class CustomTemplateAction extends AnAction {
    private Editor editor;

    public CustomTemplateAction(Editor editor, String text) {
        super(text);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        doAction(event);
    }

    /**
     * 执行action操作
     *
     * @param event
     */
    private void doAction(@NotNull AnActionEvent event) {
        CustomTemplateExportUI exportUI = new CustomTemplateExportUI();
        DialogUtil dialogUtil = new DialogUtil();
        dialogUtil.setOkBtn(exportUI.getOkBtn());
        dialogUtil.setCancelBtn(exportUI.getCancelBtn());
        confirmListener(exportUI, dialogUtil, dialogUtil.getOkBtn());
        dialogUtil.showCustomDialog(Message.EXPORT.getTitle(), exportUI.getMainPanel(), this.editor.getComponent(), new Dimension(400, 200), false);
    }

    /**
     * 导出确认事件监听
     *
     * @param exportUI   导出UI对象
     * @param dialogUtil 对话框处理工具对象
     * @param button     确认按钮
     */
    private void confirmListener(CustomTemplateExportUI exportUI, DialogUtil dialogUtil, JButton button) {
        button.addActionListener(e -> {
            // 参数验证
            if (validate(exportUI, dialogUtil)) {
                // 导出内容是否为空验证
                if (exportIsNotEmpty()) {
                    try {
                        // 导出参数拼接
                        String fileName = exportUI.getFileNameField().getText();
                        String path = exportUI.getExportBtn().getText();
                        String exportPath = path.concat(File.separator).concat(fileName);
                        String content = this.editor.getDocument().getText();
                        FileUtil fileUtil = new FileUtil();
                        fileUtil.write(exportPath, content);
                        dialogUtil.showTipsDialog(exportUI.getMainPanel(), Message.EXPORT_SUCCESS.getMsg(), Message.EXPORT_SUCCESS.getTitle());
                    } catch (Exception ex) {
                        dialogUtil.showTipsDialog(exportUI.getMainPanel(), Message.EXPORT_ERROR.getMsg().concat(ex.getMessage()), Message.EXPORT_ERROR.getTitle());
                    }
                } else {
                    dialogUtil.showTipsDialog(exportUI.getMainPanel(), Message.EXPORT_CONTENT_EMPTY.getMsg(), Message.EXPORT_CONTENT_EMPTY.getTitle());
                }
                // 资源释放
                dialogUtil.dispose();
            }
        });
    }

    /**
     * 导出相关参数验证
     *
     * @param exportUI   导出UI对象
     * @param dialogUtil 对话框处理工具对象
     * @return boolean true-验证通过，false-验证未通过
     */
    private boolean validate(CustomTemplateExportUI exportUI, DialogUtil dialogUtil) {
        // 判断导出文件名称是否为空
        String fileName = exportUI.getFileNameField().getText();
        if (StringUtils.isBlank(fileName)) {
            dialogUtil.showTipsDialog(exportUI.getMainPanel(), Message.FILENAME_EMPTY.getMsg(), Message.FILENAME_EMPTY.getTitle());
            return false;
        }
        // 判断导出路径是否为空
        String path = exportUI.getExportBtn().getText();
        if (StringUtils.isBlank(path)) {
            dialogUtil.showTipsDialog(exportUI.getMainPanel(), Message.EXPORT_PATH_EMPTY.getMsg(), Message.EXPORT_PATH_EMPTY.getTitle());
            return false;
        }
        return true;
    }

    /**
     * 判断编辑器的导出内容是否为空
     *
     * @return boolean true-不为空，false-为空
     */
    private boolean exportIsNotEmpty() {
        if (this.editor != null) {
            String content = this.editor.getDocument().getText();
            if (StringUtils.isNotBlank(content)) {
                return true;
            }
        }
        return false;
    }

}
