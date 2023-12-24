package com.lightbc.templatej.ui;

import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.CommonUtil;
import com.lightbc.templatej.utils.SelectorUtil;
import com.lightbc.templatej.utils.TemplateUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.List;

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
    private ButtonGroup group;
    private TemplateJSettings settings;
    private TemplateUtil templateUtil;

    public TemplateJCommonUI(TemplateJSettings settings, TemplateUtil templateUtil) {
        this.settings = settings;
        this.templateUtil = templateUtil;
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        initSelector();
        radioTypeListener(this.groupRadio, false);
        radioTypeListener(this.templateRadio, true);
        buttonGroup();
    }

    /**
     * 初始化下拉选择器
     */
    private void initSelector() {
        initGroupSelector();
        initGroupFileSelector();
    }

    /**
     * 初始化模板组下拉选择器
     */
    private void initGroupSelector() {
        // 获取选择器选择项
        List<String> groupList = this.templateUtil.getGroupNames();
        // 加载模板组选择器
        SelectorUtil.loadGroupSelector(getTemplateGroupSelector(), groupList, this.settings.getSelectGroup());
    }

    /**
     * 初始化模板文件下拉选择器
     */
    private void initGroupFileSelector() {
        // 默认选择模板组
        String groupName = ConfigInterface.GROUP_NAME_VALUE;
        if (StringUtils.isNotBlank(this.settings.getSelectGroup())) {
            groupName = this.settings.getSelectGroup();
        }
        Template template = this.templateUtil.getTemplate(groupName);
        List<String> fileList = null;
        if (template != null) {
            fileList = this.templateUtil.getGroupFileNames(template);
        }
        // 加载模板文件选择器
        SelectorUtil.loadGroupFileSelector(getTemplateFileSelector(), fileList, this.settings.getSelectGroupFile());
    }

    /**
     * 单选按钮组，使得一个获得焦点后，同组其它按钮失去焦点
     */
    private void buttonGroup() {
        this.group = new ButtonGroup();
        this.group.add(this.groupRadio);
        this.group.add(this.templateRadio);
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
                this.rename.setText(getExtRename(b));
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
        if (b) {
            return CommonUtil.getUUIDFileName(ConfigInterface.DEFAULT_GENERATE_EXT);
        }
        return CommonUtil.getUUIDFileName();
    }

    /**
     * 显示下拉组件
     */
    void showSelector() {
        this.addType.setVisible(false);
        this.renamePanel.setVisible(false);
    }

    /**
     * 显示重命名组件
     */
    public void showRename() {
        this.addType.setVisible(false);
    }

    /**
     * 显示复制组件
     */
    public void showCopy() {
        this.addType.setVisible(false);
    }

    /**
     * 显示新增组件
     */
    public void showAdd() {
        this.selectorPanel.setVisible(false);
    }

    /**
     * 显示删除组件
     */
    public void showDel() {
        this.addType.setVisible(false);
        this.renamePanel.setVisible(false);
    }

    /**
     * 下拉选择可可用
     */
    void enable() {
        this.templateGroupSelector.setEnabled(true);
        this.templateFileSelector.setEnabled(true);
    }

    /**
     * 下拉选择不可用
     */
    public void disable() {
        this.templateGroupSelector.setEnabled(false);
        this.templateFileSelector.setEnabled(false);
    }

    /**
     * 获取模板组名称
     *
     * @return string 模板组名称
     */
    public String getGroupName() {
        return this.getName(this.templateGroupSelector);
    }

    /**
     * 获取模板文件名称
     *
     * @return string 模板文件名称
     */
    public String getGroupFileName() {
        return this.getName(this.templateFileSelector);
    }

    /**
     * 获取选择的名称
     *
     * @param box 下拉选择框对象
     * @return string 选择项名称
     */
    private synchronized String getName(JComboBox box) {
        Object obj = box.getSelectedItem();
        return obj != null ? obj.toString() : null;
    }
}
