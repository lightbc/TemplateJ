package com.lightbc.templatej.listener;

import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.ui.TypeMapperUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 数据类型映射器事件监听
 */
public class TypeMapperListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public TypeMapperListener() {

    }

    public TypeMapperListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
    }

    /**
     * 类型映射器监听功能
     */
    public void typMapper() {
        templateJUI.getTypeMapperBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TypeMapperUI ui = new TypeMapperUI(templateJUI);
                ui.typMapper();
            }
        });
    }

}
