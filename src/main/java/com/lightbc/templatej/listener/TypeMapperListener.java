package com.lightbc.templatej.listener;

import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.ui.TypeMapperUI;

/**
 * 数据类型映射器事件监听
 */
public class TypeMapperListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public TypeMapperListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init(){
        typeMapper();
    }

    /**
     * 类型映射器监听功能
     */
    public void typeMapper() {
        templateJUI.getTypeMapperBtn().addActionListener(e -> {
            TypeMapperUI ui = new TypeMapperUI(templateJUI);
            ui.typeMapper();
        });
    }

}
