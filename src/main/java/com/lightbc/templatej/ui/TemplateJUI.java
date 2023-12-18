package com.lightbc.templatej.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.options.Configurable;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.action.EditorPopupMenuActionGroup;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.constant.CustomIcon;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.listener.*;
import com.lightbc.templatej.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * TemplateJ 模板生成工具主配置UI界面
 */
@Data
@Slf4j
public class TemplateJUI implements Configurable {
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
    private String modified;
    // 系统级别持久化设置
    private volatile TemplateJSettings settings;
    // 模板数据处理工具类
    private volatile TemplateUtil templateUtil;
    // 重命名按钮
    private JButton editName;
    // 选择器组件面板
    private JPanel selectorPanel;
    private JButton jdbcType;
    private JButton api;
    // 通用组件
    private TemplateJCommonUI commonUI;

    public TemplateJUI() {
        init();
    }

    /**
     * 功能初始化加载
     */
    private void init() {
        this.settings = TemplateJSettings.getInstance();
        this.templateUtil = new TemplateUtil(this.settings.getTemplates());
        // 加载主界面上的各个子组件
        initSelectorPanel();
        initEditorPanel();
        // 加载图标
        initIcons();
        // 初始化监听事件
        initListener();
    }

    /**
     * 初始化选择器面板
     */
    private void initSelectorPanel() {
        this.commonUI = new TemplateJCommonUI(this.settings, this.templateUtil);
        this.selectorPanel.setLayout(new BorderLayout());
        JPanel commonSelector = this.commonUI.getMainPanel();
        this.selectorPanel.add(commonSelector, BorderLayout.CENTER);
        this.commonUI.showSelector();
        this.commonUI.enable();
    }

    /**
     * 初始化编辑区域面板
     */
    private void initEditorPanel() {
        // 设置分割面板的左组件不显示
        if (this.splitPne != null) {
            this.splitPne.getLeftComponent().setVisible(false);
        }
    }

    /**
     * 事件监听
     */
    private void initListener() {
        // 重置插件
        resetPlugin();
        // 模板组选择器选择事件监听
        groupListener();
        // 模板文件选择器选择事件监听
        fileListener();
        // 复制功能事件监听
        new CopyListener(this);
        // 新增功能事件监听
        new AddListener(this);
        // 删除功能事件监听
        new DelListener(this);
        // 重命名点击事件监听
        new EditNameListener(this);
        // 预览功能事件监听
        new PreviewListener(this);
        // API接口文档配置时间监听
        new ApiDocListener(this);
        // 模板组全局配置事件监听
        new GroupConfigListener(this);
        // 数据类型映射器功能事件监听
        new TypeMapperListener(this);
        // 模板导入功能事件监听
        new ImportListener(this);
        // 模板导出功能事件监听
        new ExportListener(this);
        // 帮助功能事件监听
        new HelpListener().help(this.help);
    }

    /**
     * 编辑器编辑文档加载
     */
    private void loadEditorDoc() {
        if (StringUtils.isNotBlank(this.commonUI.getGroupName()) && TemplateUtil.isTemplateFile(this.commonUI.getGroupFileName())) {
            String groupName = this.commonUI.getGroupName();
            String fileName = this.commonUI.getGroupFileName();
            Template template = this.templateUtil.getTemplate(groupName);
            if (template != null) {
                String content = this.templateUtil.getTemplateContent(template, fileName);
                content = content != null ? content : "";
                refreshEditor(fileName, content);
            }
        } else {
            refreshEditor(ConfigInterface.DEFAULT_FILENAME, "");
        }
    }

    /**
     * 模板组下拉选择器选择事件监听
     */
    private void groupListener() {
        //模板组选择器事件监听
        this.commonUI.getTemplateGroupSelector().addActionListener(e -> {
            // 根据选择的模板组，获取该模板组下的模板文件项，并设置模板选择器的选择项
            if (StringUtils.isNotBlank(this.commonUI.getGroupName())) {
                SelectorUtil.loadGroupFileSelector(this.commonUI.getTemplateFileSelector(), this.templateUtil.getGroupFileNames(this.commonUI.getGroupName()), this.settings.getSelectGroupFile());
                this.settings.setSelectGroup(this.commonUI.getGroupName());
            }
        });
    }

    /**
     * 模板文件选择器下拉选择事件监听
     */
    private void fileListener() {
        //模板文件选择器事件监听
        this.commonUI.getTemplateFileSelector().addActionListener(e -> {
            // 根据选择的模板文件，实时更新编辑器的编辑文档内容
            if (StringUtils.isNotBlank(this.commonUI.getGroupName()) && StringUtils.isNotBlank(this.commonUI.getGroupFileName())) {
                String fileName = this.commonUI.getGroupFileName();
                loadEditorDoc();
                this.settings.setSelectGroupFile(fileName);
            }
        });
    }

    /**
     * 刷新编辑器显示
     *
     * @param fileName 当前选择的模板文件
     * @param content  编辑器的显示内容
     */
    private void refreshEditor(String fileName, String content) {
        EditorUtil util = new EditorUtil();
        JComponent editorComponent = util.getEditor(fileName, content, true);
        this.editor = util.getEditor();
        this.splitPne.setDividerLocation(0);
        this.splitPne.setDividerSize(-1);
        this.splitPne.setRightComponent(editorComponent);
        this.modified = this.editor.getDocument().getText();
        this.editor.addEditorMouseListener(new EditorMouseListener() {
            @Override
            public void mouseReleased(@NotNull EditorMouseEvent event) {
                MouseEvent e = event.getMouseEvent();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    EditorPopupMenuActionGroup.setChildren(RightKeyUtil.getPluginConfigActions(editor));
                    RightKeyUtil.disableApiDocEditorPopupMenu();
                }
            }
        });
    }

    /**
     * 刷新主UI界面
     */
    public synchronized void refresh() {
        // 保存配置数据
        this.settings.save();
        // 初始化下拉选择器
        SelectorUtil.loadGroupSelector(this.commonUI.getTemplateGroupSelector(), this.templateUtil.getGroupNames(), this.settings.getSelectGroup());
        // 加载编辑区域编辑内容
        loadEditorDoc();
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
        return this.mainPanel;
    }

    @Override
    public boolean isModified() {
        // 判断编辑器的内容是否进行修改，如已修改，apply按钮功能可用
        this.modified = this.editor.getDocument().getText();
        String oldEditorTxt = "";
        if (StringUtils.isNotBlank(this.commonUI.getGroupName()) && StringUtils.isNotBlank(this.commonUI.getGroupFileName())) {
            Template template = this.templateUtil.getTemplate(this.commonUI.getGroupName());
            oldEditorTxt = this.templateUtil.getTemplateContent(template, this.commonUI.getGroupFileName());
        }
        return TemplateUtil.isTemplateFile(this.commonUI.getGroupFileName()) && !this.modified.equals(oldEditorTxt);
    }

    @Override
    public void apply() {
        String groupName = this.commonUI.getGroupName();
        String templateFileName = this.commonUI.getGroupFileName();
        Template template = this.templateUtil.getTemplate(groupName);
        Map<String, String> fileContentMap = template.getFileContentMap();
        // 保存编辑的模板文件内容
        fileContentMap.put(templateFileName, this.modified);
        refresh();
    }

    /**
     * 重置插件
     */
    private void resetPlugin() {
        this.reset.addActionListener(e -> {
            DialogUtil dialogUtil = new DialogUtil();
            int dialog = dialogUtil.showConfirmDialog(null, Message.RESET_TEMPLATEJ.getMsg(), Message.RESET_TEMPLATEJ.getTitle());
            // 确认重置
            if (dialog == 0) {
                // 重置模板组
                this.settings.setTemplates(DefaultTemplateParams.getDefaultTemplates());
                // 重置默认选择模板组
                this.settings.setSelectGroup(DefaultTemplateParams.DEFAULT_NAME);
                // 重置默认选择模板文件
                this.settings.setSelectGroupFile(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
                this.templateUtil = new TemplateUtil(this.settings.getTemplates());
                this.settings.save();
                refresh();
            }
        });
    }

    /**
     * 初始化功能图标，使用系统自带的图标（2019.1）
     */
    private void initIcons() {
        // 编辑（模板组/模板文件）名称图标
        this.editName.setIcon(AllIcons.Actions.Edit);
        // 复制按钮图标
        this.copy.setIcon(AllIcons.General.CopyHovered);
        // 新增按钮图标
        this.add.setIcon(AllIcons.General.Add);
        // 删除按钮图标
        this.del.setIcon(AllIcons.General.Remove);
        // 模板组全局设置按钮图标
        this.configBtn.setIcon(AllIcons.General.GearPlain);
        // 帮助按钮图标
        this.help.setIcon(AllIcons.Actions.Help);
        // 导出按钮图标
        this.exportButton.setIcon(AllIcons.Actions.Upload);
        // 导入按钮图标
        this.importButton.setIcon(AllIcons.Actions.Download);
        // 预览按钮图标
        this.preview.setIcon(AllIcons.General.InspectionsEye);
        // 功能重置按钮图标
        this.reset.setIcon(AllIcons.General.Reset);

        // 自定义图标
        // API文档按钮图标
        this.api.setIcon(CustomIcon.API);
        // JdbcType按钮图标
        this.jdbcType.setIcon(CustomIcon.JDBC_TYPE_MAPPER);
        // JavaType按钮图标
        this.typeMapperBtn.setIcon(CustomIcon.JAVA_TYPE_MAPPER);
    }
}
