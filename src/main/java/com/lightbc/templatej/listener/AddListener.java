package com.lightbc.templatej.listener;

import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 新增模板事件监听
 */
@Slf4j
public class AddListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public AddListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 新增模板组/模板文件监听功能
     */

    public void add() {
        templateJUI.getAdd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this) {
                    // 加载新增UI界面
                    TemplateJCommonUI ui = new TemplateJCommonUI(templateJUI);
                    ui.setGroup(ui.buttonGroup());
                    // 新增功能，禁用模板文件选择器
                    ui.getTemplateFileSelector().setEnabled(false);
                    ui.getGroupFileLabel().setVisible(false);
                    ui.getSelectorPanel().setVisible(false);
                    // 新名称输入框，填入初始化值
                    new OperateUtil().initAddRename(ui.getRename());
                    // 新增操作对话框
                    int c = add(ui.getMainPanel());
                    // 确认新增操作
                    if (c == 0) {
                        DialogUtil dialogUtil = new DialogUtil();
                        String rename = ui.getRename().getText().trim();
                        if (rename.equals("")) {
                            dialogUtil.showTipsDialog(null, Message.ADD_CHOICE_FAIL.getMsg(), Message.ADD_CHOICE_FAIL.getTitle());
                            return;
                        }
                        int radioType = getRadioType(ui);
                        String groupName = Objects.requireNonNull(ui.getTemplateGroupSelector().getSelectedItem()).toString();
                        TemplateUtil templateUtil = templateJUI.getTemplateUtil();
                        boolean fb = false;
                        // 模板文件是否存在
                        if (radioType == 1) {
                            fb = templateUtil.existGroupFile(groupName, rename);
                        }
                        // 模板组是否存在
                        if (radioType == 2) {
                            fb = templateUtil.existGroup(rename);
                        }
                        // 已存在提示
                        if (fb) {
                            dialogUtil.showTipsDialog(null, Message.ADD_EXIST.getMsg(), Message.ADD_EXIST.getTitle());
                            return;
                        }
                        // 新增模板文件，设置默认选择项
                        if (radioType == 1) {
                            templateUtil.addGroupFile(groupName, rename, "");
                            templateJUI.getSettings().setSelectGroupName(groupName);
                            templateJUI.getSettings().setSelectGroupFile(rename);
                        }
                        // 新增模板组，设置默认选择项
                        if (radioType == 2) {
                            templateUtil.addGroup(rename);
                            templateJUI.getSettings().setSelectGroupName(rename);
                            templateJUI.getSettings().setSelectGroupFile(0);
                        }
                        // 刷新主UI界面
                        refresh(templateUtil);
                    }
                }
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
     * 刷新主UI界面
     *
     * @param templateUtil 模板数据处理工具类
     */
    private void refresh(TemplateUtil templateUtil) {
        String selectGroup = templateJUI.getSettings().getSelectGroupName();
        Object selectGroupFile = templateJUI.getSettings().getSelectGroupFile();
        templateJUI.selector(templateJUI.getTemplateGroupSelector(), templateUtil.getGroupNames(), selectGroup);
        templateJUI.selector(templateJUI.getTemplateFileSelector(), templateUtil.getGroupFileNames(selectGroup), selectGroupFile);
    }

    /**
     * 新增模板组/模板文件
     *
     * @param message 消息内容对象
     * @return int
     */
    public int add(Object message) {
        DialogUtil dialog = new DialogUtil();
        return dialog.showOperateDialog(null, message, Message.ADD_TEMPLATE.getTitle());
    }

}
