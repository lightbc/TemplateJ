package com.lightbc.templatej.listener;

import com.lightbc.templatej.ui.RenameUI;
import com.lightbc.templatej.ui.TemplateJUI;

/**
 * 模板组/模板文件，重命名按钮监听
 */
public class EditNameListener {
    private TemplateJUI templateJUI;

    public EditNameListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 重命名功能
     */
    public void editName() {
        this.templateJUI.getEditName().addActionListener((e) -> {
            // 加载新增UI界面
            RenameUI ui = new RenameUI(this.templateJUI);
            ui.rename();
        });
    }

}
