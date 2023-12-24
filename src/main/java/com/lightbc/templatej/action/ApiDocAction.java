package com.lightbc.templatej.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.PluginUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Description:API文档动作执行类
 * Package:com.lightbc.templatej.action
 *
 * @author lightbc
 * @version 1.0
 */
public class ApiDocAction extends AnAction {
    @Setter
    @Getter
    private String content;
    @Setter
    @Getter
    private String groupName;

    public ApiDocAction() {
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        doAction();
    }

    private void doAction() {
        DialogUtil dialog = new DialogUtil();
        if (StringUtils.isBlank(this.content)) {
            dialog.showTipsDialog(null, Message.API_DOC_EMPTY.getMsg(), Message.API_DOC_EMPTY.getTitle());
            return;
        }
        if (StringUtils.isNotBlank(this.groupName)) {
            PluginUtil.cacheApiDoc(this.groupName, this.content);
        }
    }

}
