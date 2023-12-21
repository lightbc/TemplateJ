package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Enumeration;

/**
 * 新增模板事件监听
 */
@Slf4j
public class AddListener {
    // 配置界面UI
    private TemplateJUI templateJUI;
    private TemplateJCommonUI commonUI;

    public AddListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        loadCommonUI();
        add();
    }

    private void loadCommonUI() {
        this.commonUI = new TemplateJCommonUI(this.templateJUI.getSettings(), this.templateJUI.getTemplateUtil());
    }

    /**
     * 新增模板组/模板文件监听功能
     */

    public void add() {
        this.templateJUI.getAdd().addActionListener(e -> {
            loadCommonUI();
            this.commonUI.disable();
            this.commonUI.showAdd();
            // 新名称输入框，填入初始化值
            this.commonUI.getRename().setText(CommonUtil.getUUIDFileName());
            // 新增操作对话框
            int c = add(this.commonUI.getMainPanel());
            // 确认新增操作
            if (c == 0) {
                DialogUtil dialogUtil = new DialogUtil();
                String rename = this.commonUI.getRename().getText().trim();
                if (rename.equals("")) {
                    dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.ADD_CHOICE_FAIL.getMsg(), Message.ADD_CHOICE_FAIL.getTitle());
                    return;
                }
                int radioType = getRadioType(commonUI);
                String groupName = this.commonUI.getGroupName();
                TemplateUtil templateUtil = this.templateJUI.getTemplateUtil();
                boolean fb = false;
                // 模板文件是否存在
                if (radioType == 1) {
                    Template template = templateUtil.getTemplate(groupName);
                    fb = templateUtil.existGroupFile(template, rename);
                }
                // 模板组是否存在
                if (radioType == 2) {
                    fb = templateUtil.existGroup(rename);
                }
                // 已存在提示
                if (fb) {
                    dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.ADD_EXIST.getMsg(), Message.ADD_EXIST.getTitle());
                    return;
                }
                // 新增模板文件，设置默认选择项
                if (radioType == 1) {
                    templateUtil.addGroupFile(groupName, rename, "");
                    this.templateJUI.getSettings().setSelectGroup(groupName);
                    this.templateJUI.getSettings().setSelectGroupFile(rename);
                }
                // 新增模板组，设置默认选择项
                if (radioType == 2) {
                    templateUtil.addGroup(rename);
                    this.templateJUI.getSettings().setSelectGroup(rename);
                    this.templateJUI.getSettings().setSelectGroupFile(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
                }
                // 刷新主UI界面
                this.templateJUI.refresh();
            }
        });
    }

    /**
     * 判断radio类型
     *
     * @param ui ui对象
     * @return 文件类型，文件-1，文件夹-2
     */

    private int getRadioType(TemplateJCommonUI ui) {
        // 文件类型
        int type = 1;
        String radioType = "";
        Enumeration<AbstractButton> em = ui.getGroup().getElements();
        while (em.hasMoreElements()) {
            AbstractButton btn = em.nextElement();
            // 获取选中的radio
            if (btn.isSelected()) {
                radioType = btn.getText();
            }
        }
        // 文件夹类型
        if (radioType.equals(ConfigInterface.DEFAULT_RADIO_GROUP_NAME)) {
            type = 2;
        }
        return type;
    }

    /**
     * 新增模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    public int add(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(this.templateJUI.getMainPanel(), message, Message.ADD_TEMPLATE.getTitle());
    }

}
