package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * 复制模板事件监听
 */
@Slf4j
public class CopyListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public CopyListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 复制模板组/模板文件监听功能
     */

    public void copy() {
        templateJUI.getCopy().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this){
                    //加载复制UI界面
                    TemplateJCommonUI ui = new TemplateJCommonUI(templateJUI);
                    //隐藏非必要组件
                    ui.getAddType().setVisible(false);
                    //新名称输入框，填入初始化值
                    new OperateUtil(ui).initRename();
                    //复制操作对话框
                    int c = copy(ui.getMainPanel());
                    //确认复制操作
                    if (c == 0) {
                        String groupName = Objects.requireNonNull(ui.getTemplateGroupSelector().getSelectedItem()).toString();
                        String fileName = Objects.requireNonNull(ui.getTemplateFileSelector().getSelectedItem()).toString();
                        String rename = ui.getRename().getText();
                        int fileIndex = ui.getTemplateFileSelector().getSelectedIndex();
                        //复制
                        TemplateUtil templateUtil = templateJUI.getTemplateUtil();
                        Template template = templateUtil.getTemplate(groupName);
                        DialogUtil dialogUtil = new DialogUtil();
                        // 模板组
                        if (fileIndex == 0) {
                            if (templateUtil.existGroup(rename)) {
                                dialogUtil.showTipsDialog(null, Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                                return;
                            }
                            templateUtil.copyGroup(rename, template);
                            templateJUI.getSettings().setSelectGroupName(rename);
                            templateJUI.getSettings().setSelectGroupFile(0);
                        }
                        // 模板文件
                        if (fileIndex > 0) {
                            if (templateUtil.existGroupFile(groupName, rename)) {
                                dialogUtil.showTipsDialog(null, Message.FILE_EXIST.getMsg(), Message.FILE_EXIST.getTitle());
                                return;
                            }
                            templateUtil.copyGroupFile(groupName, fileName, rename);
                            templateJUI.getSettings().setSelectGroupName(groupName);
                            templateJUI.getSettings().setSelectGroupFile(rename);
                        }
                        // 刷新主UI界面
                        refresh(templateUtil);
                    }
                }
            }
        });
    }

    /**
     * 刷新主UI界面
     *
     * @param templateUtil 模板数据处理工具
     */
    private void refresh(TemplateUtil templateUtil) {
        String selectGroup = templateJUI.getSettings().getSelectGroupName();
        Object selectGroupFile = templateJUI.getSettings().getSelectGroupFile();
        templateJUI.selector(templateJUI.getTemplateGroupSelector(), templateUtil.getGroupNames(), selectGroup);
        templateJUI.selector(templateJUI.getTemplateFileSelector(), templateUtil.getGroupFileNames(selectGroup), selectGroupFile);
    }

    /**
     * 复制模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    private int copy(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(null, message, Message.COPY_TEMPLATE.getTitle());
    }
}
