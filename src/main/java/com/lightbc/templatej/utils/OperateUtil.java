package com.lightbc.templatej.utils;

import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;

import javax.swing.*;
import java.util.UUID;

/**
 * 模板增删改操作工具类
 */
public class OperateUtil implements ConfigInterface {
    // 通用组件UI
    private TemplateJCommonUI ui;

    public OperateUtil() {
    }

    public OperateUtil(TemplateJCommonUI ui) {
        this.ui = ui;
    }

    /**
     * 初始化重命名输入框内容
     *
     * @return this
     */
    public OperateUtil initRename() {
        String groupName = ui.getTemplateGroupSelector().getSelectedItem().toString();
        int index = ui.getTemplateFileSelector().getSelectedIndex();
        String fileName = ui.getTemplateFileSelector().getSelectedItem().toString();
        String ext = "";
        if (fileName.indexOf(".") != -1) {
            ext = fileName.substring(fileName.indexOf("."));
            fileName = fileName.substring(0, fileName.indexOf("."));
        }
        // 重命名输入框填入当前选择的文件名称加指定后缀
        if (index > 0) {
            ui.getRename().setText(fileName.concat(ConfigInterface.DEFAULT_RENAME_SUFFIX).concat(ext));
        }
        // 重命名输入框填入当前选择的模板组名称加指定后缀
        if (index == 0) {
            ui.getRename().setText(groupName.concat(ConfigInterface.DEFAULT_RENAME_SUFFIX));
        }
        return this;
    }

    /**
     * 新增操作是初始化文件名称-UUID
     *
     * @param field 文件名输入框对象
     */
    public void initAddRename(JTextField field) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        field.setText(uuid);
    }

}
