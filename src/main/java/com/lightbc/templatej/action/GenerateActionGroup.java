package com.lightbc.templatej.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成动作组
 */
public class GenerateActionGroup extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        return getAnActions(anActionEvent);
    }

    /**
     * 生成动作组选项列表
     *
     * @param anActionEvent 动作事件
     * @return 动作对象数组
     */
    private AnAction[] getAnActions(@Nullable AnActionEvent anActionEvent) {
        PsiElement[] elements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        List<DbTable> tableList = new ArrayList<>();
        if (elements != null && elements.length > 0) {
            for (PsiElement element : elements) {
                // 判断选中的对象是否全是数据表对象，不全是，则不显示默认操作动作
                if (!(element instanceof DbTable)) {
                    return AnAction.EMPTY_ARRAY;
                }
                // 获取选中的所有数据表对象
                DbTable table = (DbTable) element;
                tableList.add(table);
            }
        }
        // 创建默认的动作组列表项
        GenerateAction generateAction = new GenerateAction("TemplateJ Generate");
        generateAction.setTableList(tableList);
        return new AnAction[]{generateAction};
    }
}
