package com.lightbc.templatej.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.Configurable;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.listener.*;
import com.lightbc.templatej.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TemplateJ 模板生成工具主配置UI界面
 */
@Data
@Slf4j
public class TemplateJUI implements Configurable {
    // 模板组下拉选择器
    private JComboBox templateGroupSelector;
    // 模板文件下拉选择器
    private JComboBox templateFileSelector;
    // 主UI界面
    private JPanel mainPanel;
    // 复制功能按钮
    private JButton copy;
    // 新增功能按钮
    private JButton add;
    // 预览功能按钮
    private JButton preview;
    // 删除功能按钮
    private JButton del;
    // 导入功能按钮
    private JButton importButton;
    // 导出功能按钮
    private JButton exportButton;
    private JLabel templateFile;
    private JLabel templateGroup;
    // 编辑器UI面板
    private JPanel editorPanel;
    private JSplitPane splitPne;
    // 模板组全局配置配置按钮
    private JButton configBtn;
    // 帮助按钮
    private JButton help;
    // 数据类型映射器
    private JButton typeMapperBtn;
    private JButton reset;
    // 编辑器
    private Editor editor;
    // 编辑器修改内容
    private String modifiedEditorContent;
    // 系统级别持久化设置
    private TemplateJSettings settings;
    // 模板数据处理工具类
    private TemplateUtil templateUtil;
    private JButton editName;

    public TemplateJUI() {
        init();
    }

    /**
     * 功能初始化加载
     */
    private void init() {
        // 设置分割面板的左组件不显示
        if (splitPne != null) {
            splitPne.getLeftComponent().setVisible(false);
        }
        // 获取系统级别持久化功能对象
        settings = TemplateJSettings.getInstance();
        templateUtil = new TemplateUtil(settings.getTemplates());

        initIcons();
        initSelector();
        listener(this);
    }

    /**
     * 初始化下拉选择器
     */
    public void initSelector() {
        // 获取选择器选择项
        List<String> groupList = templateUtil.getGroupNames();
        String selectGroupName = settings.getSelectGroupName();
        List<String> fileList = templateUtil.getGroupFileNames(selectGroupName);
        if (fileList != null) {
            fileList.remove(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
            fileList.add(0, ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
        }
        // 构建选择器
        selector(templateGroupSelector, groupList, settings.getSelectGroupName());
        selector(templateFileSelector, fileList, settings.getSelectGroupFile());

    }

    /**
     * 构建选择器
     *
     * @param box    选择器
     * @param list   选择项
     * @param select 默认选择项
     */
    public void selector(JComboBox box, List<String> list, Object select) {
        DefaultComboBoxModel model = getModel(new DefaultComboBoxModel(), list);
        //添加下拉选择器的model，并设置默认选择项
        if (model != null) {
            box.setModel(model);
            defaultSelect(box, select);
        }
    }

    /**
     * 事件监听
     *
     * @param templateJUI 监听对象
     */
    private void listener(TemplateJUI templateJUI) {
        // 重置插件
        resetApp();
        // 模板组选择器选择事件监听
        groupListener();
        // 模板文件选择器选择事件监听
        fileListener();
        // 复制功能事件监听
        new CopyListener(templateJUI).copy();
        // 新增功能事件监听
        new AddListener(templateJUI).add();
        // 删除功能事件监听
        new DelListener(templateJUI).del();
        // 预览功能事件监听
        new PreviewListener(templateJUI).preview();
        // 模板组全局配置事件监听
        new GroupConfigListener(templateJUI).config();
        // 帮助功能事件监听
        new HelpListener().help(help);
        // 数据类型映射器功能事件监听
        new TypeMapperListener(templateJUI).typMapper();
        // 模板导入功能事件监听
        new ImportListener(templateJUI).importTemplate();
        // 模板导出功能事件监听
        new ExportListener(templateJUI).exportTemplate();
        // 重命名点击事件监听
        new EditNameListener(templateJUI).editName();
    }

    /**
     * 获取下拉选择器model
     *
     * @param model 迭代model对象
     * @param items 新增元素列表
     * @return 迭代的model
     */
    DefaultComboBoxModel getModel(DefaultComboBoxModel model, List<String> items) {
        if (items != null) {
            for (String item : items) {
                model(model, item);
            }
        }
        return model;
    }

    /**
     * 获取下拉选择器的model
     *
     * @param model 迭代model对象
     * @param item  新增元素
     */
    private void model(DefaultComboBoxModel model, String item) {
        model.addElement(item);
    }

    /**
     * 设置默认选择项
     *
     * @param box    选择器组件
     * @param select 选择内容
     */
    private void defaultSelect(JComboBox box, Object select) {
        // 通过选择项名称，设置默认选择项
        if (select instanceof String) {
            defaultSelectByName(box, select.toString());
        }
        // 通过选择项下标，设置默认选择项
        if (select instanceof Integer) {
            defaultSelectByIndex(box, Integer.parseInt(select.toString()));
        }
    }

    /**
     * 通过名称设置默认选择项
     *
     * @param box    选择器
     * @param select 选择项
     */
    private void defaultSelectByName(JComboBox box, String select) {
        box.setSelectedItem(select);
    }

    /**
     * 通过选择下标设置默认选择项
     *
     * @param box   选择器
     * @param index 选择项下标
     */
    private void defaultSelectByIndex(JComboBox box, int index) {
        if (box != null && box.getModel().getSize() > 0) {
            box.setSelectedIndex(index);
        }
    }

    /**
     * 编辑器编辑文档加载
     */
    private void loadEditorDoc() {
        int index = templateFileSelector.getSelectedIndex();
        // index>0，选择文件
        if (index > 0) {
            String groupName = Objects.requireNonNull(templateGroupSelector.getSelectedItem()).toString();
            String fileName = Objects.requireNonNull(templateFileSelector.getSelectedItem()).toString();
            String content = templateUtil.getTemplateContent(groupName, fileName);
            refreshEditor(fileName, content);
        } else {
            refreshEditor(ConfigInterface.DEFAULT_FILENAME, "");
        }
    }

    /**
     * 模板组下拉选择器选择事件监听
     */
    private void groupListener() {
        //模板组选择器事件监听
        templateGroupSelector.addActionListener(e -> {
            // 根据选择的模板组，获取该模板组下的模板文件项，并设置模板选择器的选择项
            String groupName = Objects.requireNonNull(templateGroupSelector.getSelectedItem()).toString();
            List<String> fileList = templateUtil.getGroupFileNames(groupName);
            DefaultComboBoxModel fileModel = new TemplateJUI().getModel(new DefaultComboBoxModel(), fileList);
            templateFileSelector.setModel(fileModel);
            templateFileSelector.setSelectedIndex(0);
        });
    }

    /**
     * 模板文件选择器下拉选择事件监听
     */
    private void fileListener() {
        //模板文件选择器事件监听
        templateFileSelector.addActionListener(e -> {
            // 根据选择的模板文件，实时更新编辑器的编辑文档内容
            String groupName = Objects.requireNonNull(templateGroupSelector.getSelectedItem()).toString();
            String fileName = Objects.requireNonNull(templateFileSelector.getSelectedItem()).toString();
            templateFileSelector.setSelectedItem(fileName);
            loadEditorDoc();
            settings.setSelectGroupName(groupName);
            settings.setSelectGroupFile(fileName);
        });
    }

    /**
     * 刷新编辑器显示
     *
     * @param fileName 当前选择的模板文件
     * @param content  编辑器的显示内容
     */
    private void refreshEditor(String fileName, String content) {
        EditorUtil editorUtil = new EditorUtil(content);
        JComponent editorComponent = editorUtil.getEditor(fileName, true);
        editor = editorUtil.getEditor();
        splitPne.setDividerLocation(0);
        splitPne.setDividerSize(-1);
        splitPne.setRightComponent(editorComponent);
        modifiedEditorContent = editor.getDocument().getText();
        settings.setContent(modifiedEditorContent);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return ConfigInterface.PLUGIN_TEMPLATEJ_DISPLAYNAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadEditorDoc();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        // 判断编辑器的内容是否进行修改，如已修改，apply按钮功能可用
        modifiedEditorContent = editor.getDocument().getText();
        String oldEditorTxt = settings.getContent();
        int index = templateFileSelector.getSelectedIndex();
        return index > 0 && !modifiedEditorContent.equals(oldEditorTxt);
    }

    @Override
    public void apply() {
        String groupName = Objects.requireNonNull(templateGroupSelector.getSelectedItem()).toString();
        String templateFileName = Objects.requireNonNull(templateFileSelector.getSelectedItem()).toString();
        Template template = templateUtil.getTemplate(groupName);
        Map<String, String> fileContentMap = template.getFileContentMap();
        fileContentMap.put(templateFileName, modifiedEditorContent);
        settings.setContent(modifiedEditorContent);
    }

    /**
     * 重置插件
     */
    private void resetApp() {
        reset.addActionListener(e -> {
            DialogUtil dialogUtil = new DialogUtil();
            int dialog = dialogUtil.showConfirmDialog(null, Message.RESET_TEMPLATEJ.getMsg(), Message.RESET_TEMPLATEJ.getTitle());
            if (dialog == 0) {
                settings.setContent("");
                settings.setTemplates(DefaultTemplateParams.getDefaultTemplates());
                settings.setTypeMapper(DefaultTemplateParams.getDefaultTypeMapper());
                settings.setSelectGroupName(DefaultTemplateParams.DEFAULT_NAME);
                settings.setSelectGroupFile(0);
                templateUtil = new TemplateUtil(DefaultTemplateParams.getDefaultTemplates());
                initSelector();
            }
        });
    }

    /**
     * 初始化功能图标，使用系统自带的图标（2019.1）
     */
    private void initIcons() {
        // 编辑（模板组/模板文件）名称图标
        editName.setIcon(AllIcons.Actions.Edit);
        // 复制按钮图标
        copy.setIcon(AllIcons.General.CopyHovered);
        // 新增按钮图标
        add.setIcon(AllIcons.General.Add);
        // 删除按钮图标
        del.setIcon(AllIcons.General.Remove);
        // 模板组全局设置按钮图标
        configBtn.setIcon(AllIcons.General.GearPlain);
        // 帮助按钮图标
        help.setIcon(AllIcons.Actions.Help);
        // 导出按钮图标
        exportButton.setIcon(AllIcons.Actions.Upload);
        // 导入按钮图标
        importButton.setIcon(AllIcons.Actions.Download);
        // 预览按钮图标
        preview.setIcon(AllIcons.General.InspectionsEye);
        // 功能重置按钮图标
        reset.setIcon(AllIcons.General.Reset);
    }
}
