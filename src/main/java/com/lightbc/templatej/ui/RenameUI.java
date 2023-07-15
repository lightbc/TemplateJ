package com.lightbc.templatej.ui;

import com.intellij.openapi.options.Configurable;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.TemplateUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class RenameUI implements Configurable {
    //模板组下拉选择器
    private JComboBox templateGroupSelector;
    // 模板文件下拉选择器
    private JComboBox templateFileSelector;
    // 重命名
    private JTextField rename;
    // UI主面板
    private JPanel mainPanel;
    private JPanel renamePanel;
    private JPanel selectorPanel;
    private JLabel groupFileLabel;
    private ButtonGroup group;
    // 配置界面UI
    private TemplateJUI templateJUI;
    private TemplateJSettings settings;
    private String groupName;
    private TemplateUtil templateUtil;
    private Template template;
    private List<Template> templates;

    public RenameUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        this.settings = templateJUI.getSettings();
        this.groupName = getGroupName();
        this.templateUtil = new TemplateUtil(this.settings.getTemplates());
        this.template = this.templateUtil.getTemplate(this.groupName);
        this.templates = this.settings.getTemplates();
        init();
    }

    private void init() {
        initComboBoxModel();
    }

    /**
     * 初始化下拉选择器模型
     */
    private void initComboBoxModel() {
        // 初始化模板组选择器
        List<String> groupName = this.templateJUI.getTemplateUtil().getGroupNames();
        String selectGroup = Objects.requireNonNull(this.templateJUI.getTemplateGroupSelector().getSelectedItem()).toString();
        this.templateGroupSelector.setModel(this.templateJUI.getModel(new DefaultComboBoxModel(), groupName));
        this.templateGroupSelector.setSelectedItem(selectGroup);
        // 初始化模板文件选择器
        List<String> files = this.templateJUI.getTemplateUtil().getGroupFileNames(selectGroup);
        String selectFile = Objects.requireNonNull(this.templateJUI.getTemplateFileSelector().getSelectedItem()).toString();
        this.templateFileSelector.setModel(this.templateJUI.getModel(new DefaultComboBoxModel(), files));
        this.templateFileSelector.setSelectedItem(selectFile);
    }

    /**
     * 重命名
     */
    public void rename() {
        // 重命名默认的模板组/模板文件错误提示
        if (isDefaultTemplate(this.groupName, getTemplateFileName())) {
            DialogUtil dialogUtil = new DialogUtil();
            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_DEFAULT_TEMPLATE_FAIL.getMsg(), Message.RENAME_DEFAULT_TEMPLATE_FAIL.getTitle());
            return;
        }
        // 新增功能，禁用模板文件选择器
        this.getTemplateFileSelector().setEnabled(false);
        DialogUtil dialogUtil = new DialogUtil();
        int c = dialogUtil.showConfirmDialog(this.templateJUI.getMainPanel(), this.getMainPanel(), Message.RENAME_TITLE.getTitle());
        // 0：确认操作
        if (c == 0) {
            apply();
        }
    }

    /**
     * 判断是否选择的是模板文件
     *
     * @return boolean true-是，false-否
     */
    private boolean isTemplateFile() {
        Object fileSelector = this.templateJUI.getTemplateFileSelector();
        if (fileSelector != null) {
            int fileIndex = this.templateJUI.getTemplateFileSelector().getSelectedIndex();
            if (fileIndex > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取模板组名称
     *
     * @return string 模板组名称
     */
    private String getGroupName() {
        Object object = this.templateJUI.getTemplateGroupSelector().getSelectedItem();
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    /**
     * 获取模板文件名称
     *
     * @return string 模板文件名称
     */
    private String getTemplateFileName() {
        return isTemplateFile() ? this.templateJUI.getTemplateFileSelector().getSelectedItem().toString() : null;
    }

    /**
     * 判断需要删除的m模板是不是默认的模板
     *
     * @param groupName        模板组名称
     * @param templateFileName 模板文件名称
     * @return boolean 是-true，否-false
     */
    private boolean isDefaultTemplate(String groupName, String templateFileName) {
        // 是否是默认模板组
        if (ConfigInterface.GROUP_NAME_VALUE.equals(groupName.trim()) && !isTemplateFile()) {
            return true;
        }
        // 是否是默认模板组的默认模板文件
        if (ConfigInterface.GROUP_NAME_VALUE.equals(groupName.trim()) && isTemplateFile()) {
            String[] groupFile = ConfigInterface.GROUP_FILE_VALUE;
            for (String file : groupFile) {
                if (file.equals(templateFileName.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this.mainPanel;
    }

    @Override
    public boolean isModified() {
        Object groupName = this.templateJUI.getTemplateGroupSelector().getSelectedItem();
        Object templateFileName = this.templateJUI.getTemplateFileSelector().getSelectedItem();
        // 重命名模板文件，且模板文件选择为空，或重命名模板组，且模板组选择为空，返回false
        if ((isTemplateFile() && templateFileName == null) || (!isTemplateFile() && groupName == null)) {
            return false;
        }
        return isTemplateFile() ? !this.rename.equals(templateFileName.toString()) : !this.rename.getText().equals(groupName.toString());
    }

    @Override
    public void apply() {
        DialogUtil dialogUtil = new DialogUtil();
        String rename = this.getRename().getText();
        // 重命名为空提示
        if (StringUtils.isBlank(rename)) {
            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_IS_BLANK.getMsg(), Message.RENAME_IS_BLANK.getTitle());
            return;
        }

        boolean b = this.templates.contains(template);
        // 判断进行操作的模板对象是否存在
        if (b) {
            int templateIndex = this.templates.indexOf(template);
            //重命名模板组/模板文件名称
            if (isTemplateFile()) {
                List<String> groupFiles = this.templates.get(templateIndex).getGroupFiles();
                // 判断模板文件名称是否存在
                if (groupFiles.contains(rename)) {
                    dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_GROUP_FILE_EXIST.getMsg(), Message.RENAME_GROUP_FILE_EXIST.getTitle());
                    return;
                }
                Collections.replaceAll(groupFiles, getTemplateFileName(), rename);
                Map<String, String> contentMap = this.templates.get(templateIndex).getFileContentMap();
                // 包含操作的模板项
                if (contentMap.containsKey(getTemplateFileName())) {
                    String content = contentMap.get(getTemplateFileName());
                    contentMap.remove(getTemplateFileName());
                    contentMap.put(rename, content);
                }
                // 设置默认选择模板文件
                settings.setSelectGroupFile(rename);
                // 刷新模板文件选择器
                this.templateJUI.selector(this.templateJUI.getTemplateFileSelector(), this.templateUtil.getGroupFileNames(this.groupName), settings.getSelectGroupFile());
            } else {
                // 判断模板组名称是否存在
                if (this.templates != null) {
                    for (Template tp : this.templates) {
                        if (tp.getGroupName().trim().equals(rename.trim())) {
                            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_GROUP_EXIST.getMsg(), Message.RENAME_GROUP_EXIST.getTitle());
                            return;
                        }
                    }
                }
                this.templates.get(templateIndex).setGroupName(rename);
                // 设置默认选择模板组
                settings.setSelectGroupName(rename);
                // 刷新模板组选择器
                this.templateJUI.selector(this.templateJUI.getTemplateGroupSelector(), this.templateUtil.getGroupNames(), settings.getSelectGroupName());
            }
        } else {
            dialogUtil.showTipsDialog(null, Message.OPERATE_TEMPLATE_NOT_EXIST.getMsg(), Message.OPERATE_TEMPLATE_NOT_EXIST.getTitle());
            return;
        }
    }

}
