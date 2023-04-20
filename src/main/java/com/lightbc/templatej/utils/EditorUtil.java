package com.lightbc.templatej.utils;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.lightbc.templatej.interfaces.ConfigInterface;
import lombok.Getter;

import javax.swing.*;
import java.util.Properties;

/**
 * 编辑工具类
 */
public class EditorUtil {
    //editor工厂对象
    private EditorFactory factory;
    //编辑对象
    @Getter
    private Editor editor;
    //编辑内容
    private String content;
    private Project project = ProjectUtil.getProject();


    public EditorUtil() {
    }

    public EditorUtil(String content) {
        this.content = content;
    }

    /**
     * 初始化编辑对象
     *
     * @param fileName 文件名称
     * @param b        可编辑：true，只读：false
     */
    private void initEditor(String fileName, boolean b) {
        factory = EditorFactory.getInstance();
        releaseEditor();
        initEditorByType(fileName, b);
    }

    /**
     * 根据类型初始化加载编辑器
     *
     * @param fileName 模板文件名
     * @param b        可编辑-true，不可编辑-false
     */
    private void initEditorByType(String fileName, boolean b) {
        Document document = getDocument(fileName);
        if (b) {
            editable(document, project);
        } else {
            readOnly(document, getFileType(), project);
        }
        initSettings();
        highLighter(fileName, project);
    }

    /**
     * 获取编辑文档
     *
     * @param fileName 模板名称
     * @return document
     */
    private Document getDocument(String fileName) {
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        String psiFileName = fileName.concat(".").concat(ConfigInterface.EXT_VALUE);
        // 处理换行符
        if (content != null && !"".equals(content.trim())) {
            content = processContent(content);
        }
        content = content != null ? content : "";
        PsiFile psiFile = psiFileFactory.createFileFromText(psiFileName, getFileType(), content, 0, true);
        Properties properties = FileTemplateManager.getInstance(project).getDefaultProperties();
        psiFile.getViewProvider().putUserData(FileTemplateManager.DEFAULT_TEMPLATE_PROPERTIES, properties);
        return PsiDocumentManager.getInstance(project).getDocument(psiFile);
    }

    /**
     * 获取文件模板类型（vm、ft）
     *
     * @return FileType
     */
    private FileType getFileType() {
        return FileTypeManager.getInstance().getFileTypeByExtension(ConfigInterface.EXT_VALUE);
    }

    /**
     * 可编辑编辑框
     *
     * @param document 编辑文档
     * @param project  当前打开的项目
     */
    private void editable(Document document, Project project) {
        editor = factory.createEditor(document, project);
    }

    /**
     * 只读编辑框
     *
     * @param document 编辑文档
     * @param fileType 文件类型
     * @param project  当前打开的项目
     */
    private void readOnly(Document document, FileType fileType, Project project) {
        editor = factory.createEditor(document, project, fileType, true);
    }

    /**
     * 初始化编辑设置
     */
    private void initSettings() {
        EditorSettings settings = editor.getSettings();
        //关闭虚拟空间
        settings.setVirtualSpace(false);
        //关闭断点标记
        settings.setLineMarkerAreaShown(false);
        //关闭缩进指示
        settings.setIndentGuidesShown(false);
        //开启行号显示
        settings.setLineNumbersShown(true);
        //开启代码折叠
        settings.setFoldingOutlineShown(true);
        //关闭光标显示
        settings.setCaretRowShown(false);
    }

    /**
     * 高亮显示
     *
     * @param fileName 文件名称
     * @param project  工程项目
     */
    public void highLighter(String fileName, Project project) {
        String name = fileName.concat(".").concat(ConfigInterface.EXT_VALUE);
        LightVirtualFile lightVirtualFile = new LightVirtualFile(name);
        EditorHighlighterFactory factory = EditorHighlighterFactory.getInstance();
        EditorHighlighter editorHighlighter = factory.createEditorHighlighter(project, lightVirtualFile);
        ((EditorEx) editor).setHighlighter(editorHighlighter);
    }

    /**
     * 释放editor
     */
    public void releaseEditor() {
        if (editor != null) {
            factory.releaseEditor(editor);
        }
    }

    /**
     * 获取编辑器组件
     *
     * @param fileName 文件名称
     * @param b        是否获取可编辑对象，可编辑：true，不可编辑：false
     * @return JComponent 编辑器组件
     */
    public JComponent getEditor(String fileName, boolean b) {
        if (fileName == null || "".equals(fileName.trim())) {
            fileName = ConfigInterface.DEFAULT_FILENAME;
        }
        initEditor(fileName, b);
        return editor.getComponent();
    }

    /**
     * 处理显示文本中的换行符问题
     *
     * @param content 显示文本
     * @return String
     */
    private String processContent(String content) {
        return content.replaceAll("\r", "");
    }
}
