package com.lightbc.templatej.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.lightbc.templatej.enums.Message;
import lombok.Data;

import javax.swing.*;
import java.awt.*;

/**
 * 对话框工具类
 */
@Data
public class DialogUtil {
    // 编辑器
    private Editor editor;
    // 确定按钮
    private JButton okBtn;
    // 取消按钮
    private JButton cancelBtn;
    private JDialog dialog;

    /**
     * 确认对话框
     *
     * @param component 父级组件
     * @param message   消息内容
     * @param title     标题
     * @return int 确定-0
     */
    public int showConfirmDialog(Component component, Object message, String title) {
        return JOptionPane.showConfirmDialog(component, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 帮助功能对话框
     *
     * @param title     标题
     * @param component 父级组件
     */
    public void showHelpDialog(String title, JComponent component) {
        DialogBuilder builder = getDialogBuilder(title, component);
        builder.addOkAction();
        builder.dispose();
        builder.show();
    }

    /**
     * 复制、新增模板时，显示操作确认对话框
     *
     * @param component 组件
     * @param message   消息内容对象
     * @param title     标题内容
     * @return int 确定：0
     */
    public int showOperateDialog(Component component, Object message, String title) {
        return JOptionPane.showConfirmDialog(component, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 友情提示对话框
     *
     * @param component 父级组件
     * @param message   消息主体
     * @param title     标题
     */
    public void showTipsDialog(Component component, Object message, String title) {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 预览功能对话框
     *
     * @param title     标题
     * @param component 父级组件
     * @param util      编辑功能工具类
     */
    public void showPreviewDialog(String title, JComponent component, EditorUtil util) {
        previewDialogBuilder(title, component, util);
    }

    /**
     * 显示选中模板全局通用配置项配置对话框
     *
     * @param title    对话框标题
     * @param fileName 编辑文件名
     * @param content  编辑内容
     * @return int ok：0，cancel：1
     */
    public int showApiDialog(String title, String fileName, String content) {
        return showCommonDialog(title, fileName, content);
    }

    /**
     * 显示选中模板全局通用配置项配置对话框
     *
     * @param title    对话框标题
     * @param fileName 编辑文件名
     * @param content  编辑内容
     * @return int ok：0，cancel：1
     */
    public int showConfigDialog(String title, String fileName, String content) {
        return showCommonDialog(title, fileName, content);
    }

    /**
     * api接口文档&全局配置通用对话框
     *
     * @param title    对话框标题
     * @param fileName 编辑文件名
     * @param content  编辑内容
     * @return int ok：0，cancel：1
     */
    private int showCommonDialog(String title, String fileName, String content) {
        EditorUtil util = new EditorUtil();
        JComponent editor = initEditorComponent(util, fileName, content);
        Project project = ProjectUtil.getProject();
        util.highLighter(fileName, project);
        this.editor = util.getEditor();
        return configDialogBuilder(title, editor, util);
    }

    /**
     * 构建预览功能对话框
     *
     * @param title     预览对话框标题
     * @param component 显示组件
     * @param util      编辑组件工具类
     */
    private void previewDialogBuilder(String title, JComponent component, EditorUtil util) {
        DialogBuilder builder = getDialogBuilder(title, component);
        /** 避免预览查询功能，按下回车时关闭对话框
         *  对话框确认/取消按钮默认行为为关闭对话框
         *  Enter-对话框确认按钮默认行为
         *  Esc-对话框取消按钮默认行为
         */
        builder.addCancelAction();
        builder.getCancelAction().setText("Close");
        builder.addDisposable(() -> {
            if (util != null) {
                util.releaseEditor();
            }
        });
        builder.dispose();
        builder.show();
    }

    /**
     * 构建模板通用配置信息配置对话框
     *
     * @param title  配置对话框标题
     * @param editor 编辑组件
     * @param util   编辑组件工具类
     * @return int ok：0，cancel：1
     */
    private int configDialogBuilder(String title, JComponent editor, EditorUtil util) {
        DialogBuilder builder = getDialogBuilder(title, editor);
        builder.addOkAction();
        builder.addCancelAction();
        builder.addDisposable(util::releaseEditor);
        builder.dispose();
        return builder.show();

    }

    /**
     * 编辑对话框通用配置
     *
     * @param title     标题
     * @param component 显示组件
     * @return DialogBuilder 通用对话框
     */
    private DialogBuilder getDialogBuilder(String title, JComponent component) {
        Project project = ProjectUtil.getProject();
        DialogBuilder builder = new DialogBuilder(project);
        builder.setTitle(title);
        builder.setCenterPanel(component);
        return builder;
    }

    /**
     * 初始化编辑组件
     *
     * @param util     编辑功能工具类
     * @param fileName 模板文件名称
     * @param content  文档内容
     * @return JComponent
     */
    private JComponent initEditorComponent(EditorUtil util, String fileName, String content) {
        JComponent editor = util.getEditor(fileName, content, true);
        Dimension dimension = new Dimension(700, 500);
        editor.setPreferredSize(dimension);
        return editor;
    }

    /**
     * 自定义对话框
     *
     * @param title           标题
     * @param component       显示组件
     * @param parentComponent 父级组件
     * @param dimension       加载大小
     * @param b               OK按钮点击是否自动关闭，是：true，否：false
     */
    public void showCustomDialog(String title, JComponent component, JComponent parentComponent, Dimension dimension, boolean b) {
        this.dialog = new JDialog();
        this.dialog.setLayout(new BorderLayout());
        this.dialog.setTitle(title);
        this.dialog.setSize(dimension);
        this.dialog.setLocationRelativeTo(parentComponent);
        this.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel center = new JPanel(new BorderLayout());
        if (this.okBtn == null || this.cancelBtn == null) {
            showTipsDialog(null, Message.DIALOG_LOAD_ERROR.getMsg(), Message.DIALOG_LOAD_ERROR.getTitle());
            return;
        }
        // 设置原默认的对话框确认按钮
        this.dialog.getRootPane().setDefaultButton(this.okBtn);
        center.add(component, BorderLayout.CENTER);
        this.dialog.add(center, BorderLayout.CENTER);
        this.dialog.setModal(true);
        ok(this.okBtn, b);
        cancel(this.cancelBtn);
        this.dialog.setVisible(true);
    }

    /**
     * 确认事件监听
     *
     * @param button 确认按钮
     * @param b      boolean 关闭资源-true，不关闭资源-false
     */
    private void ok(JButton button, boolean b) {
        if (b) {
            button.addActionListener(e -> dispose());
        }
    }

    /**
     * 取消事件监听，关闭资源
     *
     * @param button 取消按钮
     */
    private void cancel(JButton button) {
        button.addActionListener(e -> dispose());
    }

    /**
     * 关闭资源
     */
    public void dispose() {
        if (this.dialog != null) {
            this.dialog.dispose();
        }
    }

}
