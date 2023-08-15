package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.lightbc.templatej.utils.EditorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 使用插件配置属性action
 */
public class UsePluginConfigAction extends AnAction {
    // 属性类型，0：自动预览，1：忽略全局模板配置
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
            }
        }
    }

}
