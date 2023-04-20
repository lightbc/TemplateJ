package com.lightbc.templatej.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiPackage;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.ProjectUtil;
import com.lightbc.templatej.utils.TemplateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 模板生成器UI
 */
@Data
@Slf4j
public class TemplateJGenerateUI {
    // UI主面板
    private JPanel mainPanel;
    // 模板组下拉选择器
    private JComboBox groupBox;
    // 项目模块选择器
    private JComboBox moduleBox;
    // 复选框容器面板
    private JPanel checkboxPanel;
    // 包选择器容器面板
    private JPanel packagePanel;
    // 取消按钮
    private JButton cancelBtn;
    // 确定按钮
    private JButton okBtn;
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

    public TemplateJGenerateUI() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        project = ProjectUtil.getProject();
        TemplateJSettings settings = TemplateJSettings.getInstance();
        templateUtil = new TemplateUtil(settings.getTemplates());
        loadGroupItems();
        loadProjectModules();
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
        List<String> groups = templateUtil.getGroupNames();
        groupBox.setModel(getModel(groups));
        String groupName = groupBox.getSelectedItem().toString();
        List<String> fileList = templateUtil.getGroupFileNames(groupName);
        loadCheckbox(getCheckFiles(fileList));
    }

    /**
     * 加载模块选择器选项
     */
    private void loadProjectModules() {
        List<String> list = ProjectUtil.getProjectModuleNames();
        moduleBox.setModel(getModel(list));
        String selectModule = moduleBox.getSelectedItem().toString();
        Module module = ProjectUtil.getProjectModule(project, selectModule);
        generateRoot = ProjectUtil.getModulePath(module);
    }

    /**
     * 加载包选择组件
     */
    private void loadPackage() {
        packagePanel.setLayout(new BorderLayout());
        packagePath = new TextFieldWithBrowseButton();
        packagePath.setEditable(false);
        packagePanel.add(packagePath, BorderLayout.CENTER);
    }

    /**
     * 加载保存位置选择组件
     */
    private void loadSavePath() {
        savePathPanel.setLayout(new BorderLayout());
        savePath = new TextFieldWithBrowseButton();
        savePathPanel.add(savePath, BorderLayout.CENTER);
    }

    /**
     * 加载模板文件复选框组件
     *
     * @param fileList 模板文件
     */
    private void loadCheckbox(List<String> fileList) {
        if (fileList == null || fileList.size() == 0) {
            log.warn("文件选项为空！");
            return;
        }
        // 移除复选面板的子组件
        checkboxPanel.removeAll();
        checkboxContainerPanel = new JPanel(new GridLayout(fileList.size(), 1));
        // 向复选框面板组件中添加复选框组件
        for (int i = 0; i < fileList.size(); i++) {
            String checkName = fileList.get(i);
            JCheckBox checkBox = new JCheckBox(checkName);
            checkboxContainerPanel.add(checkBox);
        }
        // 组件重绘显示
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
        JScrollPane scrollPane = new JScrollPane(checkboxContainerPanel);
        checkboxPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 获取下拉选择器的选择数据模型
     *
     * @param items 元素
     * @return DefaultComboBoxModel 选择器数据模型
     */
    private DefaultComboBoxModel getModel(List<String> items) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < items.size(); i++) {
            model.addElement(items.get(i));
        }
        return model;
    }

    /**
     * 模板组选择事件监听
     */
    private void groupSelectListener() {
        groupBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = groupBox.getSelectedItem().toString();
                List<String> fileList = templateUtil.getGroupFileNames(groupName);
                loadCheckbox(getCheckFiles(fileList));
                // 移除勾选
                allCheck.setSelected(false);
                generateTips.setSelected(false);
            }
        });
    }

    /**
     * 包选择器事件监听
     */
    private void packageChooseListener() {
        packagePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packageChoose();
            }
        });
    }

    /**
     * 保存位置组件事件监听
     */
    private void savePathListener() {
        savePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = moduleBox.getSelectedItem().toString();
                savePathChoose(name);
            }
        });
    }

    /**
     * 模板全选事件监听
     */
    private void allCheckListener() {
        allCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allCheck.isSelected()) {
                    allCheck(true);
                } else {
                    allCheck(false);
                }
            }
        });
    }

    /**
     * 模板全选
     *
     * @param b 选择-true，不选-false
     */
    private void allCheck(boolean b) {
        Component[] components = checkboxContainerPanel.getComponents();
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
        PackageChooserDialog chooserDialog = new PackageChooserDialog(Message.PACKAGE_CHOOSER_TITLE.getTitle(), project);
        chooserDialog.show();
        PsiPackage psiPackage = chooserDialog.getSelectedPackage();
        // 反显选择的包路径
        if (psiPackage != null) {
            String pkName = psiPackage.getQualifiedName();
            packagePath.setText(pkName);
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
            savePath.setText(virtualFile.getPath());
        }
    }

    /**
     * 保存位置
     *
     * @return String 模板生成路径
     */
    public String getGeneratePath() {
        return savePath.getText();
    }

    /**
     * 获取默认虚拟文件对象
     *
     * @param name 选择的模块名称
     * @return VirtualFile
     */
    private VirtualFile getDefaultVirtualFile(String name) {
        VirtualFileManager manager = VirtualFileManager.getInstance();
        Module module = ProjectUtil.getProjectModule(project, name);
        String modulePath = ProjectUtil.getModulePath(module);
        if (modulePath != null && !"".equals(modulePath)) {
            return manager.findFileByUrl(modulePath);
        }
        return ProjectUtil.getProject().getBaseDir();
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

}
