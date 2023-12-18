package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Description:API文档动作执行类
 * Package:com.lightbc.templatej.action
 *
 * @author lightbc
 * @version 1.0
 */
public class ApiDocAction extends AnAction {
    public static final String ID = "Com.Lightbc.Templatej.ApiDocAction";

    public ApiDocAction() {
        ActionManager.getInstance().unregisterAction(ID);
    }

    public ApiDocAction(String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

    }

    public static void setAction() {

    }
}
