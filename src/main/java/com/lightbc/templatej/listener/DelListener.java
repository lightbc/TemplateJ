package com.lightbc.templatej.listener;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 删除模板事件监听
 */
@Slf4j
public class DelListener {
    // 配置界面UI
    private TemplateJUI templateJUI;
    private TemplateJCommonUI commonUI;

    public DelListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void loadCommonUI() {
        this.commonUI = new TemplateJCommonUI(this.templateJUI.getSettings(), this.templateJUI.getTemplateUtil());
    }

    private void init(){
        loadCommonUI();
        del();
    }

    /**
     * 删除模板组/模板文件监听功能
     */

    public void del() {
        this.templateJUI.getDel().addActionListener(e -> {
            loadCommonUI();
            this.commonUI.disable();
            //隐藏不需要的通用组件
            this.commonUI.showDel();
            //删除操作对话框
            int db = del(this.commonUI.getMainPanel());
            //确认删除操作
            if (db == 0) {
                String groupName = this.commonUI.getGroupName();
                int groupIndex = this.commonUI.getTemplateGroupSelector().getSelectedIndex();
                String fileName = this.commonUI.getGroupFileName();
                int fileIndex = this.commonUI.getTemplateFileSelector().getSelectedIndex();
                DialogUtil dialogUtil = new DialogUtil();
                // 默认模板判断，不能删除
                boolean idt = isDefaultTemplate(groupName, fileName);
                if (idt) {
                    dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.DEL_DEFAULT_TEMPLATE_FAIL.getMsg(), Message.DEL_DEFAULT_TEMPLATE_FAIL.getTitle());
                    return;
                }
                // 模板删除提示
                int d = dialogUtil.showConfirmDialog(this.templateJUI.getMainPanel(), Message.DEL_CONTINUE.getMsg(), Message.DEL_CONTINUE.getTitle());
                if (d != 0) {
                    return;
                }
                TemplateUtil templateUtil = this.templateJUI.getTemplateUtil();
                // 删除模板组
                if (fileIndex == 0) {
                    templateUtil.delGroup(groupName);
                    String gn = this.commonUI.getTemplateGroupSelector().getItemAt(groupIndex - 1).toString();
                    this.templateJUI.getSettings().setSelectGroup(gn);
                    this.templateJUI.getSettings().setSelectGroupFile(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
                }
                // 删除模板文件
                if (fileIndex > 0) {
                    templateUtil.delGroupFile(groupName, fileName);
                    this.templateJUI.getSettings().setSelectGroup(groupName);
                    String sgf = this.commonUI.getTemplateFileSelector().getItemAt(fileIndex - 1).toString();
                    this.templateJUI.getSettings().setSelectGroupFile(sgf);
                }
                //刷新主UI界面
                this.templateJUI.refresh();
            }
        });
    }

    /**
     * 删除模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    private int del(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(this.templateJUI.getMainPanel(), message, Message.DEL_TEMPLATE.getTitle());
    }

    /**
     * 判断需要删除的m模板是不是默认的模板
     *
     * @param groupName   模板组名称
     * @param delFileName 删除模板名称
     * @return boolean 是-true，否-false
     */
    private boolean isDefaultTemplate(String groupName, String delFileName) {
        boolean b = false;
        // 是否是默认模板组
        if (groupName.equals(ConfigInterface.GROUP_NAME_VALUE) && !TemplateUtil.isTemplateFile(delFileName)) {
            b = true;
        }
        // 是否是默认模板组的默认模板文件
        if (groupName.equals(ConfigInterface.GROUP_NAME_VALUE) && TemplateUtil.isTemplateFile(delFileName)) {
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
