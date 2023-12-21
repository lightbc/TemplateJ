package com.lightbc.templatej.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 模板生成器UI
 */
@Data
@Slf4j
public class TemplateJGenerateUI {
    // UI主面板
    private JPanel mainPanel;
    // 项目模块选择器
    private JComboBox<String> moduleBox;
    // 取消按钮
    private JButton cancelBtn;
    // 确定按钮
    private JButton okBtn;
    private JPanel commonPanel;
    private TemplateUtil templateUtil;
    private Project project;
    private Map<String, Module> moduleMap;
    // 兼容多模块项目工程，保存选择不同模块时的配置信息
    private Map<String, TemplateJGenerateCommonUI> commonUIMaps;
    // 模块名称
    private String moduleName;

    public TemplateJGenerateUI() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        this.commonUIMaps = new HashMap<>();
        this.project = ProjectUtil.getProject();
        TemplateJSettings settings = TemplateJSettings.getInstance();
        this.templateUtil = new TemplateUtil(settings.getTemplates());
        this.loadProjectModules();
        this.loadGenerateCommonUI();
        this.moduleSelectListener();
    }


    /**
     * 加载模块选择器选项
     */
    private void loadProjectModules() {
        this.moduleMap = ProjectUtil.getModuleMap();
        List<String> list = new ArrayList<>(this.moduleMap.keySet());
        SelectorUtil.loadSelector(this.moduleBox, list, null);
    }

    /**
     * 模块选择器选择事件监听
     */
    private void moduleSelectListener() {
        this.moduleBox.addActionListener(e -> loadGenerateCommonUI());
    }

    /**
     * 加载模板生成器通用UI对象
     */
    private synchronized void loadGenerateCommonUI() {
        // 通用对象已存在，则返回，未存在则重新生成
        Module module = getSelectModule();
        if (this.commonUIMaps.containsKey(module.getName())) {
            TemplateJGenerateCommonUI ui = this.commonUIMaps.get(module.getName());
            updateCommonUI(ui);
            return;
        }
        TemplateJGenerateCommonUI ui = new TemplateJGenerateCommonUI(this);
        String rootPath = ProjectUtil.getModulePath(module);
        ui.setGenerateRoot(rootPath);
        ui.getSavePath().setText(rootPath);
        this.commonUIMaps.put(this.moduleName, ui);
        updateCommonUI(ui);
    }

    /**
     * 更新重绘通用UI
     *
     * @param commonUI 通用UI对象
     */
    private void updateCommonUI(TemplateJGenerateCommonUI commonUI) {
        if (this.commonPanel != null) {
            this.commonPanel.removeAll();
        }
        assert this.commonPanel != null;
        this.commonPanel.setLayout(new BorderLayout());
        this.commonPanel.add(commonUI.getMainPanel(), BorderLayout.CENTER);
        this.commonPanel.revalidate();
        this.commonPanel.repaint();
    }

    /**
     * 获取选择模块
     *
     * @return module
     */
    public Module getSelectModule() {
        this.moduleName = Objects.requireNonNull(this.moduleBox.getSelectedItem()).toString();
        return this.moduleMap.get(moduleName);
    }

}
