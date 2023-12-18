package com.lightbc.templatej.utils;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.lightbc.templatej.action.ApiDocAction;
import com.lightbc.templatej.action.CustomTemplateAction;
import com.lightbc.templatej.action.EditorPopupMenuActionGroup;
import com.lightbc.templatej.action.UsePluginConfigAction;
import com.lightbc.templatej.interfaces.ActionNameInterface;
import cucumber.api.java.it.Ma;

/**
 * 鼠标右键功能处理工具类
 */
public class RightKeyUtil {
    private static ActionManager MANAGER = ActionManager.getInstance();

    /**
     * 获取插件主UI界面编辑器的右键菜单action项
     *
     * @param editor 编辑器对象
     * @return anaction[]
     */
    public static AnAction[] getPluginConfigActions(Editor editor) {
        UsePluginConfigAction autoPreview = new UsePluginConfigAction(editor, ActionNameInterface.AUTO_PREVIEW, 0);
        UsePluginConfigAction ignoreGlobal = new UsePluginConfigAction(editor, ActionNameInterface.IGNORE_GLOBAL, 1);
        UsePluginConfigAction customDataSource = new UsePluginConfigAction(editor, ActionNameInterface.CUSTOM_DATASOURCE, 2);
        return new AnAction[]{autoPreview, ignoreGlobal, customDataSource};
    }

    /**
     * 获取自定义模板（非数据接口类模板）右键菜单action项
     *
     * @param editor 编辑器对象
     * @return anaction[]
     */
    public static AnAction[] getCustomTemplateExportActions(Editor editor) {
        CustomTemplateAction export = new CustomTemplateAction(editor, ActionNameInterface.CUSTOM_TEMPLATE_EXPORT);
        return new AnAction[]{export};
    }

    public static void enableApiDocEditorPopupMenu(String groupName, String content) {
        ApiDocAction action = (ApiDocAction) MANAGER.getAction(ApiDocAction.ID);
        action.setContent(content);
        action.setGroupName(groupName);
        action.getTemplatePresentation().setEnabled(true);
        EditorPopupMenuActionGroup.setChildren(null);
    }

    public static void disableApiDocEditorPopupMenu() {
        MANAGER.getAction(ApiDocAction.ID).getTemplatePresentation().setEnabled(false);
    }

    public static void disableEditorPopupMenu() {
        disableApiDocEditorPopupMenu();
        EditorPopupMenuActionGroup.setChildren(null);
    }
}
