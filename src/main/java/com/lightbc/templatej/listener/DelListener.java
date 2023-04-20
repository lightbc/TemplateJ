package com.lightbc.templatej.listener;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 删除模板事件监听
 */
@Slf4j
public class DelListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public DelListener() {
    }

    public DelListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 删除模板组/模板文件监听功能
     */

    public void del() {
        templateJUI.getDel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this){
                    //加载删除UI界面
                    TemplateJCommonUI ui = new TemplateJCommonUI(templateJUI);
                    //隐藏不需要的通用组件
                    ui.getRenamePanel().setVisible(false);
                    ui.getAddType().setVisible(false);

                    //删除操作对话框
                    int db = del(ui.getMainPanel());
                    //确认删除操作
                    if (db == 0) {
                        String groupName = ui.getTemplateGroupSelector().getSelectedItem().toString();
                        int groupIndex = ui.getTemplateGroupSelector().getSelectedIndex();
                        String fileName = ui.getTemplateFileSelector().getSelectedItem().toString();
                        int fileIndex = ui.getTemplateFileSelector().getSelectedIndex();
                        DialogUtil dialogUtil = new DialogUtil();
                        // 默认模板判断，不能删除
                        boolean idt = isDefaultTemplate(groupName, fileName, fileIndex);
                        if (idt) {
                            dialogUtil.showTipsDialog(null, Message.DEL_DEFAULT_TEMPLATE_FAIL.getMsg(), Message.DEL_DEFAULT_TEMPLATE_FAIL.getTitle());
                            return;
                        }
                        // 模板删除提示
                        int d = dialogUtil.showConfirmDialog(null, Message.DEL_CONTINUE.getMsg(), Message.DEL_CONTINUE.getTitle());
                        if (d != 0) {
                            return;
                        }
                        TemplateUtil templateUtil = templateJUI.getTemplateUtil();
                        // 删除模板组
                        if (fileIndex == 0) {
                            templateUtil.delGroup(groupName);
                            String gn = ui.getTemplateGroupSelector().getItemAt(groupIndex - 1).toString();
                            templateJUI.getSettings().setSelectGroupName(gn);
                            templateJUI.getSettings().setSelectGroupFile(0);
                        }
                        // 删除模板文件
                        if (fileIndex > 0) {
                            templateUtil.delGroupFile(groupName, fileName);
                            templateJUI.getSettings().setSelectGroupName(groupName);
                            String sgf = ui.getTemplateFileSelector().getItemAt(fileIndex - 1).toString();
                            templateJUI.getSettings().setSelectGroupFile(sgf);
                        }
                        //刷新主UI界面
                        refresh(templateUtil);
                    }
                }
            }
        });
    }

    /**
     * 刷新主UI界面
     *
     * @param templateUtil
     */
    private void refresh(TemplateUtil templateUtil) {
        String selectGroup = templateJUI.getSettings().getSelectGroupName();
        Object selectGroupFile = templateJUI.getSettings().getSelectGroupFile();
        templateJUI.selector(templateJUI.getTemplateGroupSelector(), templateUtil.getGroupNames(), selectGroup);
        templateJUI.selector(templateJUI.getTemplateFileSelector(), templateUtil.getGroupFileNames(selectGroup), selectGroupFile);
    }

    /**
     * 删除模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    public int del(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(null, message, Message.DEL_TEMPLATE.getTitle());
    }

    /**
     * 判断需要删除的m模板是不是默认的模板
     *
     * @param groupName   模板组名称
     * @param delFileName 删除模板名称
     * @param fileIndex   选择模板下标
     * @return boolean 是-true，否-false
     */
    private boolean isDefaultTemplate(String groupName, String delFileName, int fileIndex) {
        boolean b = false;
        // 是否是默认模板组
        if (groupName.equals(ConfigInterface.GROUP_NAME_VALUE) && fileIndex == 0) {
            b = true;
        }
        // 是否是默认模板组的默认模板文件
        if (groupName.equals(ConfigInterface.GROUP_NAME_VALUE) && fileIndex > 0) {
            String[] groupFile = ConfigInterface.GROUP_FILE_VALUE;
            for (String file : groupFile) {
                if (delFileName.equals(file)) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

}
