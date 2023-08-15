package com.lightbc.templatej.utils;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.lightbc.templatej.action.CustomTemplateAction;
import com.lightbc.templatej.action.UsePluginConfigAction;
import com.lightbc.templatej.interfaces.ActionNameInterface;

/**
 * 鼠标右键功能处理工具类
 */
public class RightKeyUtil {

    /**
     * 获取插件主UI界面编辑器的右键菜单action项
     *
     * @param editor 编辑器对象
     * @return anaction[]
     */
    public static AnAction[] getPluginConfigActions(Editor editor) {
        UsePluginConfigAction autoPreview = new UsePluginConfigAction(editor, ActionNameInterface.AUTO_PREVIEW, 0);
        UsePluginConfigAction ignoreGlobal = new UsePluginConfigAction(editor, ActionNameInterface.IGNORE_GLOBAL, 1);
        return new AnAction[]{autoPreview, ignoreGlobal};
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
}
