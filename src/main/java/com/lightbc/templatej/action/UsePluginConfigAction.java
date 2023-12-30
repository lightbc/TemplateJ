package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 使用插件配置属性action
 */
public class UsePluginConfigAction extends AnAction {
    // 属性类型，0：自动预览，1：忽略全局模板配置，2：自定义预览，提供数源导入功能
    private int type;
    private Editor editor;
    private static final String PROP_KEY_PREFIX = "## ";
    private static final String DEFAULT_SPLIT = ":";
    private static final boolean DEFAULT_BOOLEAN_VALUE = true;
    private static final String AUTO_PREVIEW_KEY = "TemplateJ-Auto-Preview";
    private static final String IGNORE_GLOBAL_KEY = "TemplateJ-Ignore-Global";
    private static final String CUSTOM_DATASOURCE = "TemplateJ-Custom-DataSource";

    public UsePluginConfigAction(Editor editor, String text, int type) {
        super(text);
        this.editor = editor;
        this.type = type;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        doAction();
    }

    /**
     * 执行action操作
     */
    private void doAction() {
        if (this.editor != null) {
            // 编辑器文档对象
            Document document = this.editor.getDocument();
            // 文档内容
            String documentContent = document.getText();
            switch (type) {
                case 0:
                    // 在编辑的模板内容首行添加自动预览代码
                    String apk = PROP_KEY_PREFIX + AUTO_PREVIEW_KEY + DEFAULT_SPLIT;
                    // 移除已添加的自动预览属性代码
                    documentContent = removeProp(documentContent, apk);
                    String autoPreview = apk + DEFAULT_BOOLEAN_VALUE;
                    String autoPreviewContent = autoPreview + "\n" + documentContent;
                    EditorUtil.processDocument(document, autoPreviewContent);
                    break;
                case 1:
                    // 在编辑的模板内容首行添忽略全局模板配置代码
                    String igk = PROP_KEY_PREFIX + IGNORE_GLOBAL_KEY + DEFAULT_SPLIT;
                    // 移除忽略全局配置属性代码
                    documentContent = removeProp(documentContent, igk);
                    String ignoreGlobal = igk + DEFAULT_BOOLEAN_VALUE;
                    String ignoreContent = ignoreGlobal + "\n" + documentContent;
                    EditorUtil.processDocument(document, ignoreContent);
                    break;
                case 2:
                    // 向非数据接口类型自定义模板文件，添加自定义数据来源
                    String selectImportPath = FileUtil.getVirtualFilePathByFileType(ConfigInterface.CUSTOM_DATASOURCE_FILE_TYPE);
                    if (StringUtils.isNotBlank(selectImportPath)) {
                        String dpk = PROP_KEY_PREFIX + CUSTOM_DATASOURCE + DEFAULT_SPLIT;
                        // 移除自定义数据源属性代码
                        documentContent = removeProp(documentContent, dpk);
                        String dataSourcePath = dpk + selectImportPath;
                        String dataSourceContent = dataSourcePath + "\n" + documentContent;
                        EditorUtil.processDocument(document, dataSourceContent);
                    } else {
                        DialogUtil dialog = new DialogUtil();
                        dialog.showTipsDialog(this.editor.getComponent(), Message.CUSTOM_DATASOURCE_IMPORT_PATH_WARN.getMsg(), Message.CUSTOM_DATASOURCE_IMPORT_PATH_WARN.getTitle());
                    }
                    break;
            }
        }
    }

    /**
     * 移除属性
     *
     * @param content 文本内容
     * @param rStr    移除内容
     */
    private String removeProp(String content, String rStr) {
        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(rStr)) {
            String[] lines = content.split("\n");
            if (lines.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (String line : lines) {
                    if (StringUtils.isNotBlank(line) && line.trim().toUpperCase().startsWith(rStr.toUpperCase())) {
                        continue;
                    }
                    builder.append(line).append("\n");
                }
                return builder.toString();
            }
        }
        return content;
    }

}
