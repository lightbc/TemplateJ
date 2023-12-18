package com.lightbc.templatej.listener;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.lightbc.templatej.action.ApiDocAction;
import com.lightbc.templatej.action.EditorPopupMenuActionGroup;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.ProjectUtil;
import com.lightbc.templatej.utils.RightKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

/**
 * Description:接口文档配置事件监听
 * Package:com.lightbc.templatej.listener
 *
 * @author lightbc
 * @version 1.0
 */
public class ApiDocListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public ApiDocListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        this.templateJUI.getApi().addActionListener(e -> apiDoc());
    }

    /**
     * 编辑接口文档内容
     */
    private void apiDoc() {
        String groupName = this.templateJUI.getCommonUI().getGroupName();
        if (StringUtils.isNotBlank(groupName)) {
            String title = Message.API_DCO.getTitle().concat(groupName);

            // API文档文件名（含默认拓展名）
            String fileName = groupName.concat(ConfigInterface.API_DOC_TYPE);
            Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
            String content = template.getApiDoc();
            DialogUtil util = new DialogUtil();

            // 显示API文档配置对话框
            int c = util.showApiDialog(title, fileName, content);
            // 编辑区域右键事件监听
            editorPopupMenuListener(util.getEditor(), fileName);
            //ok按钮按下后进行的后续操作
            if (c == 0) {
                String editedContent = util.getEditor().getDocument().getText();
                if (StringUtils.isNotBlank(editedContent)) {
                    // 保存编辑后的内容
                    template.setApiDoc(editedContent);
                    // 刷新UI显示
                    this.templateJUI.refresh();
                } else {
                    util.showTipsDialog(null, Message.API_DOC_EMPTY.getMsg(), Message.API_DOC_EMPTY.getTitle());
                }
            }
        }
    }

    private void addEditorPopupMenu(Editor editor, String fileName) {
        if (editor != null) {
            Document document = editor.getDocument();
            Project project = ProjectUtil.getProject();
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            String name = psiFile.getName();
            if (fileName.equals(name)) {
                ActionManager manager = ActionManager.getInstance();
                ApiDocAction action = new ApiDocAction("Effect Preview");
                manager.registerAction(ApiDocAction.ID, action);
            }
        }
    }

    /**
     * 编辑器右键属性菜单监听
     */
    private void editorPopupMenuListener(Editor editor, String fileName) {
        editor.addEditorMouseListener(new EditorMouseListener() {
            @Override
            public void mouseReleased(@NotNull EditorMouseEvent event) {
                MouseEvent e = event.getMouseEvent();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    addEditorPopupMenu(editor, fileName);
                }
            }
        });
    }
}
