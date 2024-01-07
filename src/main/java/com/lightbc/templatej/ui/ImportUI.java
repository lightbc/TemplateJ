package com.lightbc.templatej.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板导入UI界面
 */
@Data
public class ImportUI {
    private JPanel mainPanel;
    private JButton ok;
    private JButton cancel;
    // 模板组名称
    private JTextField groupName;
    private JPanel importPathPanel;
    private JPanel templateFilePanel;
    private JPanel importTemplateGroupPanel;
    private JPanel globalConfigPanel;
    private JCheckBox javaType;
    private JCheckBox jdbcType;
    private JPanel apiDocPanel;
    // 导入路径选择组件
    private TextFieldWithBrowseButton browseButton;
    private JPanel importFileContainer;
    // 文件父级路径
    private String parentDir;
    // 全局配置文件选择组件
    private TextFieldWithBrowseButton globalConfigButton;
    // API接口文档选择组件
    private TextFieldWithBrowseButton apiDocButton;
    private TemplateJUI templateJUI;
    private DialogUtil dialogUtil;

    public ImportUI(TemplateJUI templateJUI, DialogUtil dialogUtil) {
        this.templateJUI = templateJUI;
        this.dialogUtil = dialogUtil;
        init();
    }

    private void init() {
        hideComponent();
        loadComponent();
        importPathSelectListener();
        importGlobalConfigListener();
        importApiDocListener();
    }

    /**
     * 隐藏组件
     */
    private void hideComponent() {
        this.importTemplateGroupPanel.setVisible(false);
    }

    /**
     * 显示组件
     */
    private void showComponent() {
        this.globalConfigButton.setText("");
        String importPath = this.browseButton.getText();
        this.groupName.setText(getPathParentNodeName(importPath));
        if (this.importFileContainer != null) {
            this.importFileContainer.removeAll();
        }
        List<String> fileList = getCheckListElements(importPath);
        // 显示导入的文件列表复选框，默认勾选状态
        showGroupFileCheckList(fileList);
        if (fileList != null && fileList.size() > 0) {
            this.importTemplateGroupPanel.setVisible(true);
        } else {
            // 无文件模板项时，组件置空
            if (this.importFileContainer != null) {
                this.importFileContainer.removeAll();
                this.importFileContainer.revalidate();
                this.importFileContainer.repaint();
            }
            DialogUtil dialogUtil = new DialogUtil();
            dialogUtil.showTipsDialog(this.mainPanel, Message.IMPORT_TEMPLATE_FILE_NO_EXIST.getMsg(), Message.IMPORT_TEMPLATE_FILE_NO_EXIST.getTitle());
        }
        // 类型映射器复选框默认显示状态
        showTypeMapper(importPath);
    }

    /**
     * 加载组件
     */
    private void loadComponent() {
        // 加载导入路径选择组件
        this.importPathPanel.setLayout(new BorderLayout());
        this.browseButton = new TextFieldWithBrowseButton();
        this.browseButton.setEditable(false);
        this.importPathPanel.add(this.browseButton, BorderLayout.CENTER);

        // 加载全局配置文件导入组件
        this.globalConfigPanel.setLayout(new BorderLayout());
        this.globalConfigButton = new TextFieldWithBrowseButton();
        this.globalConfigButton.setEditable(false);
        this.globalConfigPanel.add(this.globalConfigButton, BorderLayout.CENTER);

        // 加载API接口文档导入组件
        this.apiDocPanel.setLayout(new BorderLayout());
        this.apiDocButton = new TextFieldWithBrowseButton();
        this.apiDocButton.setEditable(false);
        this.apiDocPanel.add(this.apiDocButton, BorderLayout.CENTER);
    }

    /**
     * 显示模板文件复选框列表
     *
     * @param elements 模板文件
     */
    private void showGroupFileCheckList(List<String> elements) {
        if (elements != null && elements.size() > 0) {
            // 移除默认模板选择项
            elements.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
            // 移除子组件
            this.templateFilePanel.removeAll();
            this.importFileContainer = new JPanel(new GridLayout(elements.size(), 1));
            for (String element : elements) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setText(element);
                checkBox.setSelected(true);
                this.importFileContainer.add(checkBox);
            }
            // 重绘组件
            this.templateFilePanel.revalidate();
            this.templateFilePanel.repaint();
            JScrollPane scrollPane = new JScrollPane(this.importFileContainer);
            this.templateFilePanel.setLayout(new BorderLayout());
            this.templateFilePanel.add(scrollPane, BorderLayout.CENTER);
        }
    }

    /**
     * 导入路径选择组件事件监听
     */
    private void importPathSelectListener() {
        this.browseButton.addActionListener(e -> {
            String selectImportPath = FileUtil.getVirtualFilePathOrDir();
            if (StringUtils.isNotBlank(selectImportPath)) {
                this.browseButton.setText(selectImportPath);
                // 选择模板导入文件夹路径后，调整对话框大小及位置
                if (this.dialogUtil != null) {
                    this.dialogUtil.resize(550, 370, this.mainPanel);
                }
                showComponent();
            }
        });
    }

    /**
     * 导入全局配置文件组件事件监听
     */
    private void importGlobalConfigListener() {
        this.globalConfigButton.addActionListener(e -> {
            String selectGlobalConfigPath = FileUtil.getVirtualFilePathByFileType(ConfigInterface.PLUGIN_DEFAULT_EXT_WITH_NO_DOT);
            if (StringUtils.isNotBlank(selectGlobalConfigPath)) {
                // 全局配置文件格式的进行获取并显示，否则提示
                if (isGlobalConfig(selectGlobalConfigPath)) {
                    this.globalConfigButton.setText(selectGlobalConfigPath);
                } else {
                    DialogUtil dialogUtil = new DialogUtil();
                    dialogUtil.showTipsDialog(this.mainPanel, Message.IMPORT_IS_NOT_GLOBAL_CONFIG.getMsg(), Message.IMPORT_IS_NOT_GLOBAL_CONFIG.getTitle());
                }
            }
        });
    }

    /**
     * 导入API接口文档组件事件监听
     */
    private void importApiDocListener() {
        this.apiDocButton.addActionListener(e -> {
            String selectApiDocPath = FileUtil.getVirtualFilePathByFileType(ConfigInterface.API_DOC_EXT_WITH_NO_DOT);
            if (StringUtils.isNotBlank(selectApiDocPath)) {
                // API接口文档文件格式的进行获取并显示，否则提示
                if (selectApiDocPath.lastIndexOf(ConfigInterface.API_DOC_EXT) != -1) {
                    this.apiDocButton.setText(selectApiDocPath);
                } else {
                    DialogUtil dialogUtil = new DialogUtil();
                    dialogUtil.showTipsDialog(this.mainPanel, Message.IMPORT_IS_NOT_API_DOC.getMsg(), Message.IMPORT_IS_NOT_API_DOC.getTitle());
                }
            }
        });
    }

    /**
     * 判断选择的文件是否是全局配置文件的指定格式
     *
     * @param path 文件路径
     * @return boolean true-是，false-否
     */
    private boolean isGlobalConfig(String path) {
        try {
            if (FileUtil.isFile(path)) {
                File file = new File(path);
                String gn = this.groupName.getText();
                if (StringUtils.isNotBlank(gn) && file.getName().equals(gn.concat(ConfigInterface.PLUGIN_DEFAULT_EXT))) {
                    return true;
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }


    /**
     * 获取路径的上级目录名称
     *
     * @param path 文件路径
     * @return string 上级目录名称
     */
    private String getPathParentNodeName(String path) {
        File file = new File(path);
        String nodeName = null;
        // 移除路径两边的空格
        if (StringUtils.isNotBlank(path)) {
            path = path.trim();
        }
        // 文件
        if (FileUtil.isFile(path)) {
            String parentPath = file.getParent();
            nodeName = parentPath.substring(parentPath.lastIndexOf(FileUtil.getSeparator(parentPath)));
            this.parentDir = parentPath.substring(0, parentPath.lastIndexOf(FileUtil.getSeparator(parentPath)));
        }
        // 文件夹
        if (FileUtil.isDir(path)) {
            nodeName = path.substring(path.lastIndexOf(FileUtil.getSeparator(path)));
            String lastChar = path.substring(path.length() - 1);
            if (lastChar.equals("/") || lastChar.equals("\\")) {
                this.parentDir = path.substring(0, path.length() - 1);
            } else {
                this.parentDir = path;
            }
        }
        return nodeName != null ? nodeName.substring(1) : null;
    }


    /**
     * 获取复选框列表元素
     *
     * @param path 文件路径
     * @return list<string>复选框元素项
     */
    private List<String> getCheckListElements(String path) {
        List<String> elements = null;
        File file = new File(path);
        if (FileUtil.isFile(path)) {
            elements = new ArrayList<>();
            addElement(file, elements);
        }
        if (FileUtil.isDir(path)) {
            elements = new ArrayList<>();
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    // 添加选择文件夹下的文件
                    if (f.isFile()) {
                        addElement(f, elements);
                    }
                }
            }
        }
        return elements;
    }

    /**
     * 添加元素
     *
     * @param file 文件
     * @param list 元素项数组
     */
    private void addElement(File file, List<String> list) {
        String name = file.getName();
        String gn = "";
        if (this.groupName != null && StringUtils.isNotBlank(this.groupName.getText())) {
            gn = this.groupName.getText();
        }
        // 匹配导入的文件中是否包含全局配置文件，存在则默认显示
        if (name.equals(gn.concat(ConfigInterface.PLUGIN_DEFAULT_EXT))) {
            this.globalConfigButton.setText(file.getAbsolutePath());
            return;
        }
        // 匹配导入的文件中是否包含API接口文档文件，存在则默认显示
        if (name.equals(gn.concat(ConfigInterface.API_DOC_EXT))) {
            this.apiDocButton.setText(file.getAbsolutePath());
            return;
        }
        // 移除*.tj文件
        if (name.lastIndexOf(ConfigInterface.PLUGIN_DEFAULT_EXT) == -1) {
            list.add(name);
        }
    }

    /**
     * 类型映射器复选框默认显示
     *
     * @param importPath 模板导入路径
     */
    private void showTypeMapper(String importPath) {
        if (StringUtils.isNotBlank(importPath)) {
            String javaTypeFilePath = importPath.concat(File.separator).concat(this.javaType.getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
            String jdbcTypeFilePath = importPath.concat(File.separator).concat(this.jdbcType.getText()).concat(ConfigInterface.PLUGIN_DEFAULT_EXT);
            File javaTypeFile = new File(javaTypeFilePath);
            File jdbcTypeFile = new File(jdbcTypeFilePath);
            // JavaType文件存在，复选框可以编辑，否则不可编辑，导入使用默认值
            if (javaTypeFile.exists() && javaTypeFile.isFile()) {
                this.getJavaType().setSelected(true);
            } else {
                this.getJavaType().setEnabled(false);
            }
            // JdbcType文件存在，复选框可以编辑，否则不可编辑，导入使用默认值
            if (jdbcTypeFile.exists() && jdbcTypeFile.isFile()) {
                this.getJdbcType().setSelected(true);
            } else {
                this.jdbcType.setEnabled(false);
            }
        }
    }
}
