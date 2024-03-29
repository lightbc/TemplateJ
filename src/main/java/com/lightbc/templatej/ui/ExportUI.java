package com.lightbc.templatej.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.FileUtil;
import com.lightbc.templatej.utils.SelectorUtil;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 模板导出UI界面
 */
@Data
public class ExportUI {
    // 模板组选择组件
    private JComboBox templateGroup;
    // 全选复选框
    private JCheckBox allCheck;
    private JPanel exportPath;
    private JPanel mainPanel;
    private JButton cancel;
    private JButton ok;
    private JPanel exportFilePanel;
    private JPanel globalConfigPanel;
    private JCheckBox javaType;
    private JCheckBox jdbcType;
    private JCheckBox apiDoc;
    private TemplateJUI templateJUI;
    // 模板导出位置选择组件
    private TextFieldWithBrowseButton browseButton;
    private JPanel exportFileContainer;
    // 全局配置文件复选框组件
    private JCheckBox globalBox;

    public ExportUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    /**
     * 功能初始化
     */
    private void init() {
        loadTemplateGroup();
        templateGroupSelectorListener();
        loadComponent();
    }

    /**
     * 加载模板组选择器选择项
     */
    private void loadTemplateGroup() {
        List<String> groupList = this.templateJUI.getTemplateUtil().getGroupNames();
        SelectorUtil.loadGroupSelector(this.templateGroup, groupList, null);
    }

    /**
     * 模板组选择器选择事件监听
     */
    private void templateGroupSelectorListener() {
        showGroupFileCheckList(groupFileList());
        this.templateGroup.addActionListener((e) -> {
            // 显示导出模板复选框列表
            showGroupFileCheckList(groupFileList());
            // 重置
            this.allCheck.setSelected(false);
            this.globalBox.setSelected(false);
            loadCheckBoxDefaultName();
            this.browseButton.setText("");
        });
    }

    /**
     * 模板文件项
     *
     * @return list<string> 模板文件
     */
    private List<String> groupFileList() {
        Object groupNameObj = this.templateGroup.getSelectedItem();
        if (groupNameObj != null) {
            String groupName = groupNameObj.toString();
            Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
            return this.templateJUI.getTemplateUtil().getGroupFileNames(template);
        }
        return null;
    }

    /**
     * 显示模板文件复选框列表组件
     *
     * @param elements 显示元素项
     */
    private void showGroupFileCheckList(List<String> elements) {
        if (elements != null && elements.size() > 0) {
            // 移除默认模板选择项
            elements.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
            // 移除子组件
            this.exportFilePanel.removeAll();
            this.exportFileContainer = new JPanel(new GridLayout(elements.size(), 1));
            for (String element : elements) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setText(element);
                this.exportFileContainer.add(checkBox);
            }
            // 重绘组件
            this.exportFilePanel.revalidate();
            this.exportFilePanel.repaint();
            JScrollPane scrollPane = new JScrollPane(this.exportFileContainer);
            this.exportFilePanel.setLayout(new BorderLayout());
            this.exportFilePanel.add(scrollPane, BorderLayout.CENTER);
            allCheckListener();
        } else {
            if (this.exportFileContainer != null) {
                this.exportFileContainer.removeAll();
            }
        }
    }

    /**
     * 全选事件监听
     */
    private void allCheckListener() {
        this.allCheck.addActionListener((e) -> allCheck(this.allCheck.isSelected()));
    }

    /**
     * 全选触发
     *
     * @param isCheck 是否全选，true-是，false-否
     */
    private void allCheck(boolean isCheck) {
        Component[] components = this.exportFileContainer.getComponents();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    checkBox.setSelected(isCheck);
                }
            }
        }
    }

    /**
     * 加载组件
     */
    private void loadComponent() {
        this.exportPath.setLayout(new BorderLayout());
        this.browseButton = new TextFieldWithBrowseButton();
        this.exportPath.add(browseButton, BorderLayout.CENTER);
        loadCheckBoxDefaultName();
        exportPathSelectListener();
    }

    /**
     * 加载复选框默认名称
     */
    private void loadCheckBoxDefaultName() {
        Object groupName = this.templateGroup.getSelectedItem();
        if (groupName != null) {
            String name = groupName.toString();
            // 设置模板组全局配置文件导出名称
            this.globalBox.setText(name.concat(ConfigInterface.PLUGIN_DEFAULT_EXT));
            // 设置模板组API接口文档文件导出名称
            this.apiDoc.setText(name.concat(ConfigInterface.API_DOC_EXT));
        }
    }

    /**
     * 文件导出组件选择事件监听
     */
    private void exportPathSelectListener() {
        this.browseButton.addActionListener((e) -> {
            String selectSavePath = FileUtil.getVirtualFileDir();
            if (selectSavePath != null) {
                this.browseButton.setText(selectSavePath);
            }
        });
    }

}
