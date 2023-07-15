package com.lightbc.templatej.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.ProjectUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    // 导入路径选择组件
    private TextFieldWithBrowseButton browseButton;
    private JPanel importFileContainer;
    // 文件父级路径
    private String parentDir;
    // 全局配置文件选择组件
    private TextFieldWithBrowseButton globalConfigButton;

    private TemplateJUI templateJUI;

    public ImportUI() {
        init();
    }

    public ImportUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        hideComponent();
        loadComponent();
        importPathSelectListener();
        importGlobalConfigListener();
        groupNameChangeListener();
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
        showGroupFileCheckList(fileList, true);
        if (fileList != null && fileList.size() > 0) {
            this.importTemplateGroupPanel.setVisible(true);
        } else {
            // 无文件模板项是，组件置空
            if (this.importFileContainer != null) {
                this.importFileContainer.removeAll();
                this.importFileContainer.revalidate();
                this.importFileContainer.repaint();
            }
            DialogUtil dialogUtil = new DialogUtil();
            dialogUtil.showTipsDialog(this.mainPanel, Message.IMPORT_TEMPLATE_FILE_NO_EXIST.getMsg(), Message.IMPORT_TEMPLATE_FILE_NO_EXIST.getTitle());
        }
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
    }

    /**
     * 显示模板文件复选框列表
     *
     * @param elements 模板文件
     * @param isCheck  是否勾选，true-是，false-否
     */
    private void showGroupFileCheckList(List<String> elements, boolean isCheck) {
        if (elements != null && elements.size() > 0) {
            // 移除默认模板选择项
            if (elements.contains(ConfigInterface.DEFAULT_GROUP_FILE_VALUE)) {
                elements.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
            }
            // 移除子组件
            this.templateFilePanel.removeAll();
            this.importFileContainer = new JPanel(new GridLayout(elements.size(), 1));
            for (String element : elements) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setText(element);
                checkBox.setSelected(isCheck);
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
            String selectImportPath = getImportPath();
            if (StringUtils.isNotBlank(selectImportPath)) {
                this.browseButton.setText(selectImportPath);
                showComponent();
            }
        });
    }

    /**
     * 模板组名称修改事件监听
     */
    private void groupNameChangeListener() {
        this.groupName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String gn = groupName.getText();
                String globalPath = globalConfigButton.getText();
                if (StringUtils.isNotBlank(globalPath)) {
                    if (isFile(globalPath)) {
                        File file = new File(globalPath);
                        String name = file.getName();
                        String fileName = name.substring(0, name.indexOf(ConfigInterface.PLUGIN_DEFAULT_EXT));
                        // 选择的全局配置文件和命名的模板组名称不一致，置空显示内容
                        if (!gn.equals(fileName)) {
                            globalConfigButton.setText("");
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取导入模板路径
     *
     * @return string 导入路径
     */
    private String getImportPath() {
        VirtualFile virtualFile = ProjectUtil.getProject().getProjectFile();
        // 导入单个文件夹/文件
        virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(), ProjectUtil.getProject(), virtualFile);
        if (virtualFile != null) {
            return virtualFile.getPath();
        }
        return null;
    }

    /**
     * 导入全局配置文件组件事件监听
     */
    private void importGlobalConfigListener() {
        this.globalConfigButton.addActionListener(e -> {
            String selectGlobalConfigPath = getImportPath();
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
     * 判断选择的文件是否是全局配置文件的指定格式
     *
     * @param path 文件路径
     * @return boolean true-是，false-否
     */
    private boolean isGlobalConfig(String path) {
        try {
            if (isFile(path)) {
                File file = new File(path);
                String gn = this.groupName.getText();
                if (StringUtils.isNotBlank(gn) && file.getName().trim().equals(gn.concat(ConfigInterface.PLUGIN_DEFAULT_EXT))) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 根据路径判断是否是文件
     *
     * @param path 文件路径
     * @return boolean 是-true，false-否
     */
    private boolean isFile(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 根据路径判断是否是文件夹
     *
     * @param path 文件路径
     * @return boolean true-是，false-否
     */
    private boolean isDir(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                return true;
            }
        } catch (Exception e) {
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
        if (isFile(path)) {
            String parentPath = file.getParent();
            nodeName = parentPath.substring(parentPath.lastIndexOf(getSeparator(parentPath)));
            this.parentDir = parentPath.substring(0, parentPath.lastIndexOf(getSeparator(parentPath)));
        }
        // 文件夹
        if (isDir(path)) {
            nodeName = path.substring(path.lastIndexOf(getSeparator(path)));
            String lastChar = path.substring(path.length() - 1);
            if (lastChar.equals("/") || lastChar.equals("\\")) {
                this.parentDir = path.substring(0, path.length() - 1);
            } else {
                this.parentDir = path;
            }
        }
        return nodeName.substring(1);
    }

    /**
     * 动态匹配分隔符
     *
     * @param path 文件路径
     * @return string 路径分隔符
     */
    private String getSeparator(String path) {
        if (path != null && path.lastIndexOf("/") == -1) {
            return "\\";
        }
        return "/";
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
        if (isFile(path)) {
            elements = new ArrayList<>();
            addElement(file, elements);
        }
        if (isDir(path)) {
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
            gn = this.groupName.getText().trim();
        }
        // 移除*.tj文件
        if (name.trim().lastIndexOf(ConfigInterface.PLUGIN_DEFAULT_EXT) == -1) {
            list.add(name);
        }
        // 匹配导入的文件中是否包含全局配置文件，存在则默认显示
        if (name.trim().equals(gn.concat(ConfigInterface.PLUGIN_DEFAULT_EXT))) {
            this.globalConfigButton.setText(file.getAbsolutePath());
        }
    }

}
