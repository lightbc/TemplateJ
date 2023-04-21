package com.lightbc.templatej.ui;

import com.lightbc.templatej.interfaces.ConfigInterface;
import lombok.Data;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 通用显示内容UI
 */
@Data
public class TemplateJCommonUI {
    //模板组下拉选择器
    private JComboBox templateGroupSelector;
    // 模板文件下拉选择器
    private JComboBox templateFileSelector;
    // 重命名
    private JTextField rename;
    // UI主面板
    private JPanel mainPanel;
    private JPanel renamePanel;
    private JRadioButton groupRadio;
    private JRadioButton templateRadio;
    private JPanel addType;
    private JPanel selectorPanel;
    private JLabel groupFileLabel;
    private ButtonGroup group;
    // 配置界面UI
    private TemplateJUI templateJUI;

    public TemplateJCommonUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initComboBoxModel();
        radioTypeListener(groupRadio, false);
        radioTypeListener(templateRadio, true);
    }

    /**
     * 单选按钮组，使得一个获得焦点后，同组其它按钮失去焦点
     *
     * @return ButtonGroup
     */
    public ButtonGroup buttonGroup() {
        group = new ButtonGroup();
        group.add(groupRadio);
        group.add(templateRadio);
        return group;
    }

    /**
     * 初始化下拉选择器模型
     */
    private void initComboBoxModel() {
        // 初始化模板组选择器
        List<String> groupName = templateJUI.getTemplateUtil().getGroupNames();
        String selectGroup = Objects.requireNonNull(templateJUI.getTemplateGroupSelector().getSelectedItem()).toString();
        templateGroupSelector.setModel(templateJUI.getModel(new DefaultComboBoxModel(), groupName));
        templateGroupSelector.setSelectedItem(selectGroup);
        // 初始化模板文件选择器
        List<String> files = templateJUI.getTemplateUtil().getGroupFileNames(selectGroup);
        String selectFile = Objects.requireNonNull(templateJUI.getTemplateFileSelector().getSelectedItem()).toString();
        templateFileSelector.setModel(templateJUI.getModel(new DefaultComboBoxModel(), files));
        templateFileSelector.setSelectedItem(selectFile);
    }

    /**
     * 单选事件监听
     *
     * @param button 单选按钮
     * @param b      是否生成拓展名，是-true，否-false
     */
    private void radioTypeListener(JRadioButton button, boolean b) {
        button.addActionListener(e -> {
            if (button.isSelected()) {
                rename.setText(getExtRename(b));
            }
        });
    }

    /**
     * 获取带有默认拓展名的文件名
     *
     * @param b 是否带拓展名，是-true，否-false
     * @return string
     */
    private String getExtRename(boolean b) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        if (b) {
            uuid = uuid.concat(ConfigInterface.DEFAULT_GENERATE_EXT);
        }
        return uuid;
    }

}
