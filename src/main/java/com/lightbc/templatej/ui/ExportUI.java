package com.lightbc.templatej.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.ProjectUtil;
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
    private JPanel exportItemsPanel;
    private TemplateJUI templateJUI;
    // 模板导出位置选择组件
    private TextFieldWithBrowseButton browseButton;
    private JPanel exportFileContainer;
    // 全局配置文件复选框组件
    private JCheckBox globalBox;

    public ExportUI() {
        init();
    }

    public ExportUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

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
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if (groupList != null && groupList.size() > 0) {
            for (String ele : groupList) {
                model.addElement(ele);
            }
        }
        this.templateGroup.setModel(model);
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
            Object selectGroupName = this.templateGroup.getSelectedItem();
            if (selectGroupName != null) {
                String checkTxt = selectGroupName.toString().concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
                this.globalBox.setText(checkTxt);
            }
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
            return this.templateJUI.getTemplateUtil().getGroupFileNames(groupName);
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
            if (elements.contains(ConfigInterface.DEFAULT_GROUP_FILE_VALUE)) {
                elements.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
            }
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
     * 全局配置文件复选框组件
     */
    private void globalComponent() {
        this.globalConfigPanel.setLayout(new GridLayout(1, 1));
        Object globalObj = this.templateGroup.getSelectedItem();
        if (globalObj != null) {
            String globalName = globalObj.toString().concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
            globalBox = new JCheckBox(globalName);
            this.globalConfigPanel.add(globalBox);
        }
    }

    /**
     * 加载组件
     */
    private void loadComponent() {
        this.exportPath.setLayout(new BorderLayout());
        this.browseButton = new TextFieldWithBrowseButton();
        this.exportPath.add(browseButton, BorderLayout.CENTER);
        globalComponent();
        exportPathSelectListener();
    }

    /**
     * 文件导出组件选择事件监听
     */
    private void exportPathSelectListener() {
        this.browseButton.addActionListener((e) -> {
            String selectSavePath = getExportPath();
            if (selectSavePath != null) {
                this.browseButton.setText(selectSavePath);
            }
        });
    }

    /**
     * 获取导出路径
     *
     * @return string 导出路径
     */
    private String getExportPath() {
        VirtualFile virtualFile = ProjectUtil.getProject().getProjectFile();
        // 只能选择文件夹
        virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), ProjectUtil.getProject(), virtualFile);
        if (virtualFile != null) {
            return virtualFile.getPath();
        }
        return null;
    }

}
