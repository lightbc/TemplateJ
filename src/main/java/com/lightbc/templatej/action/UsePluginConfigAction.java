package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.EditorUtil;
import com.lightbc.templatej.utils.ProjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 使用插件配置属性action
 */
public class UsePluginConfigAction extends AnAction {
    // 属性类型，0：自动预览，1：忽略全局模板配置，2：自定义预览，提供数源导入功能
    private int type;
    private Editor editor;

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
                    String autoPreview = "## TemplateJ-Auto-Preview:true";
                    String autoPreviewContent = autoPreview + "\n" + documentContent;
                    EditorUtil.processDocument(document, documentContent, autoPreviewContent);
                    break;
                case 1:
                    // 在编辑的模板内容首行添忽略全局模板配置代码
                    String ignoreGlobal = "## TemplateJ-Ignore-Global:true";
                    String ignoreContent = ignoreGlobal + "\n" + documentContent;
                    EditorUtil.processDocument(document, documentContent, ignoreContent);
                    break;
                case 2:
                    // 向非数据接口类型自定义模板文件，添加自定义数据来源
                    String selectImportPath = getImportDataSourcePath();
                    if (StringUtils.isNotBlank(selectImportPath)) {
                        String dataSourcePath = "## TemplateJ-Custom-DataSource:" + selectImportPath;
                        String dataSourceContent = dataSourcePath + "\n" + documentContent;
                        EditorUtil.processDocument(document, documentContent, dataSourceContent);
                    } else {
                        DialogUtil dialog = new DialogUtil();
                        dialog.showTipsDialog(this.editor.getComponent(), Message.CUSTOM_DATASOURCE_IMPORT_PATH_WARN.getMsg(), Message.CUSTOM_DATASOURCE_IMPORT_PATH_WARN.getTitle());
                    }
            }
        }
    }

    /**
     * 获取导入数源文件路径
     *
     * @return string 导入路径
     */
    private String getImportDataSourcePath() {
        VirtualFile virtualFile = ProjectUtil.getProject().getProjectFile();
        // 导入json格式文件
        virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor("json"), ProjectUtil.getProject(), virtualFile);
        if (virtualFile != null) {
            return virtualFile.getPath();
        }
        return null;
    }

}
