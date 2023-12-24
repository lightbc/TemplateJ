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

    private void init() {
        javaTypeMapper();
        jdbcTypeMapper();
    }

    /**
     * javaType类型映射器功能监听
     */
    private void javaTypeMapper() {
        this.templateJUI.getTypeMapperBtn().addActionListener(e -> {
            TypeMapperUI ui = new TypeMapperUI(this.templateJUI, 0);
            ui.typeMapper();
        });
    }

    /**
     * jdbcType类型映射器功能监听
     */
    private void jdbcTypeMapper() {
        this.templateJUI.getJdbcType().addActionListener(e -> {
            TypeMapperUI ui = new TypeMapperUI(this.templateJUI, 1);
            ui.typeMapper();
        });
    }

}
