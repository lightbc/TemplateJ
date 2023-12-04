package com.lightbc.templatej.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiPackage;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * 模板生成器UI
 */
@Data
@Slf4j
public class TemplateJGenerateCommonUI {
    // UI主面板
    private JPanel mainPanel;
    // 模板组下拉选择器
    private JComboBox<String> groupBox;
    // 复选框容器面板
    private JPanel checkboxPanel;
    // 包选择器容器面板
    private JPanel packagePanel;
    // 全选复选框
    private JCheckBox allCheck;
    // 生成过程中提示信息复选框
    private JCheckBox generateTips;
    private JPanel savePathPanel;
    // 包选择器系统组件-2019.1
    private TextFieldWithBrowseButton packagePath;
    // 保存位置系统组件-2019.1
    private TextFieldWithBrowseButton savePath;
    // 生成的根目录
    private String generateRoot;
    // 模板文件复选按钮组容器面板
    private JPanel checkboxContainerPanel;
    // 模板数据处理工具类
    private TemplateUtil templateUtil;
    private Project project;
    private Map<String, Module> moduleMap;
    private TemplateJGenerateUI generateUI;

    public TemplateJGenerateCommonUI(TemplateJGenerateUI generateUI) {
        this.generateUI = generateUI;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        this.project = ProjectUtil.getProject();
        TemplateJSettings settings = TemplateJSettings.getInstance();
        this.templateUtil = new TemplateUtil(settings.getTemplates());
        loadGroupItems();
        loadPackage();
        loadSavePath();
        groupSelectListener();
        packageChooseListener();
        savePathListener();
        allCheckListener();
    }

    /**
     * 加载模板组选择器选项
     */
    private void loadGroupItems() {
        List<String> groups = this.templateUtil.getGroupNames();
        SelectorUtil.loadGroupSelector(this.groupBox, groups, null);
        Template template = this.templateUtil.getTemplate(getGroupName());
        if (template != null) {
            List<String> fileList = this.templateUtil.getGroupFileNames(template);
            loadCheckbox(getCheckFiles(fileList));
        }
    }


    /**
     * 加载包选择组件
     */
    private void loadPackage() {
        this.packagePanel.setLayout(new BorderLayout());
        this.packagePath = new TextFieldWithBrowseButton();
        this.packagePanel.add(this.packagePath, BorderLayout.CENTER);
    }

    /**
     * 加载保存位置选择组件
     */
    private void loadSavePath() {
        this.savePathPanel.setLayout(new BorderLayout());
        this.savePath = new TextFieldWithBrowseButton();
        this.savePathPanel.add(this.savePath, BorderLayout.CENTER);
    }

    /**
     * 加载模板文件复选框组件
     *
     * @param fileList 模板文件
     */
    @SuppressWarnings("UndesirableClassUsage")
    private void loadCheckbox(List<String> fileList) {
        // 移除复选面板的子组件
        this.checkboxPanel.removeAll();
        this.checkboxContainerPanel = new JPanel(new GridLayout(fileList.size(), 1));
        // 向复选框面板组件中添加复选框组件
        for (String checkName : fileList) {
            JCheckBox checkBox = new JCheckBox(checkName);
            this.checkboxContainerPanel.add(checkBox);
        }
        // 组件重绘显示
        this.checkboxPanel.revalidate();
        this.checkboxPanel.repaint();
        JScrollPane scrollPane = new JScrollPane(this.checkboxContainerPanel);
        this.checkboxPanel.add(scrollPane, BorderLayout.CENTER);
    }


    /**
     * 模板组选择事件监听
     */
    private void groupSelectListener() {
        this.groupBox.addActionListener(e -> {
            Template template = this.templateUtil.getTemplate(getGroupName());
            if (template != null) {
                List<String> fileList = this.templateUtil.getGroupFileNames(template);
                loadCheckbox(getCheckFiles(fileList));
            }
        });
    }

    /**
     * 获取模板组名称
     *
     * @return string 模板组名称
     */
    private String getGroupName() {
        return this.groupBox.getSelectedItem() != null ? this.groupBox.getSelectedItem().toString() : null;
    }

    /**
     * 包选择器事件监听
     */
    private void packageChooseListener() {
        this.packagePath.addActionListener(e -> packageChoose());
    }

    /**
     * 保存位置组件事件监听
     */
    private void savePathListener() {
        savePath.addActionListener(e -> {
            String name = Objects.requireNonNull(this.generateUI.getModuleBox().getSelectedItem()).toString();
            savePathChoose(name);
        });
    }

    /**
     * 模板全选事件监听
     */
    private void allCheckListener() {
        this.allCheck.addActionListener(e -> {
            if (this.allCheck.isSelected()) {
                allCheck(true);
            } else {
                allCheck(false);
            }
        });
    }

    /**
     * 模板全选
     *
     * @param b 选择-true，不选-false
     */
    private void allCheck(boolean b) {
        Component[] components = this.checkboxContainerPanel.getComponents();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                JCheckBox box = (JCheckBox) component;
                box.setSelected(b);
            }
        }
    }

    /**
     * 包选择器
     */
    private void packageChoose() {
        PackageChooserDialog chooserDialog = new PackageChooserDialog(Message.PACKAGE_CHOOSER_TITLE.getTitle(), this.generateUI.getSelectModule());
        chooserDialog.show();
        PsiPackage psiPackage = chooserDialog.getSelectedPackage();
        // 反显选择的包路径
        if (psiPackage != null) {
            String pkName = psiPackage.getQualifiedName();
            this.packagePath.setText(pkName);
            if (!"".equals(pkName.trim())) {
                this.generateRoot = getCompletePath();
                this.savePath.setText(this.generateRoot);
            }
        }
    }

    /**
     * 选择保存位置
     *
     * @param name 选择的模块名称
     */
    private void savePathChoose(String name) {
        VirtualFile virtualFile = getDefaultVirtualFile(name);
        virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), ProjectUtil.getProject(), virtualFile);
        if (virtualFile != null) {
            this.savePath.setText(virtualFile.getPath());
        }
    }

    /**
     * 保存位置
     *
     * @return String 模板生成路径
     */
    public String getGeneratePath() {
        return this.savePath.getText();
    }

    /**
     * 获取默认虚拟文件对象
     *
     * @param name 选择的模块名称
     * @return VirtualFile
     */
    private VirtualFile getDefaultVirtualFile(String name) {
        VirtualFileManager manager = VirtualFileManager.getInstance();
        Module module = ProjectUtil.getProjectModule(this.project, name);
        String modulePath = ProjectUtil.getModulePath(module);
        if (modulePath != null && !"".equals(modulePath.trim())) {
            return manager.findFileByUrl(modulePath);
        }
        return null;
    }

    /**
     * 获取有效模板
     *
     * @param files 模板选择器模板项
     * @return list<string> 处理结果
     */
    private List<String> getCheckFiles(List<String> files) {
        files.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
        return files;
    }

    /**
     * 查找模块资源目录
     *
     * @return string
     */
    private String findModuleSources() {
        DialogUtil dialog = new DialogUtil();
        // 获取选择的模块路径，判断路径是否存在
        Module module = this.generateUI.getSelectModule();
        String modulePath = ProjectUtil.getModulePath(module);
        File file = new File(modulePath);
        if (!file.exists()) {
            dialog.showTipsDialog(null, Message.MODULE_PATH_LOAD_ERROR.getMsg(), Message.MODULE_PATH_LOAD_ERROR.getTitle());
            return null;
        }
        // 获取项目工程的默认结构信息，获取资源生成的默认目录路径
        VirtualFile moduleFile = VfsUtil.findFileByIoFile(file, true);
        VirtualFile findFile = VfsUtil.findRelativeFile(moduleFile, "src", "main", "java");
        if (findFile != null && findFile.isDirectory()) {
            return findFile.getPath();
        }
        // 未发现默认项目结构，默认创建该目录结构路径，并返回路径信息
        FileUtil fileUtil = new FileUtil();
        String defaultSourcePath = modulePath.concat(File.separator).concat("src").concat(File.separator).concat("main").concat(File.separator).concat("java");
        fileUtil.createDirs(defaultSourcePath);
        return defaultSourcePath;
    }

    /**
     * 获取完整的保存路径
     *
     * @return string
     */
    private String getCompletePath() {
        String moduleSourcesPath = findModuleSources();
        String pPath = this.packagePath.getText();
        if (!"".equals(pPath.trim())) {
            String nPackagePath = pPath.replaceAll("\\.", Matcher.quoteReplacement("/"));
            return moduleSourcesPath.concat("/").concat(nPackagePath);
        }
        return moduleSourcesPath;
    }

}
