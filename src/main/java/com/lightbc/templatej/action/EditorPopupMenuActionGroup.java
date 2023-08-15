package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lightbc.templatej.interfaces.ActionNameInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 编辑器属性菜单action group
 */
public class EditorPopupMenuActionGroup extends ActionGroup {
    private static AnAction[] CHILDRENS;

    public EditorPopupMenuActionGroup() {
        super(ActionNameInterface.EDITOR_POPUP_MENU_GROUP, true);
        setChildren(null);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent event) {
        return CHILDRENS;
    }

    /**
     * 动态设置action group 子菜单项
     *
     * @param actions 动作组
     */
    public static void setChildren(AnAction[] actions) {
        if (actions != null) {
            CHILDRENS = actions;
        } else {
            CHILDRENS = AnAction.EMPTY_ARRAY;
        }
    }
}
