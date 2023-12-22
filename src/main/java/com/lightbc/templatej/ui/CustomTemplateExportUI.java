package com.lightbc.templatej.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.lightbc.templatej.utils.FileUtil;
import lombok.Data;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义模板（非数据接口类模板）导出UI界面
 */
@Data
public class CustomTemplateExportUI {
    private JPanel mainPanel;
    // 文件名称
    private JTextField fileNameField;
    private JPanel exportPanel;
    // 确认按钮
    private JButton okBtn;
    // 取消按钮
    private JButton cancelBtn;
    // 导出路径选择器
    private TextFieldWithBrowseButton exportBtn;

    public CustomTemplateExportUI() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        loadExportButton();
    }

    /**
     * 加载导出路径选择器
     */
    private void loadExportButton() {
        this.exportPanel.setLayout(new BorderLayout());
        this.exportBtn = new TextFieldWithBrowseButton();
        this.exportPanel.add(this.exportBtn, BorderLayout.CENTER);
        exportButtonListener();
    }

    /**
     * 导出路径选择器操作监听
     */
    private void exportButtonListener() {
        this.exportBtn.addActionListener(e -> this.exportBtn.setText(FileUtil.getVirtualFileDir()));
    }

}
