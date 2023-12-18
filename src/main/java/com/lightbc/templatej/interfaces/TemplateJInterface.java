package com.lightbc.templatej.interfaces;

public interface TemplateJInterface {
    // 直接单模板生成/预览，不依赖数据表，TRUE-是，FALSE-否
    String AUTO_PREVIEW = "TEMPLATEJ-AUTO-PREVIEW";
    // 是否不引用全局配置，TRUE-是，FALSE-否
    String IGNORE_GLOBAL = "TEMPLATEJ-IGNORE-GLOBAL";
    // 引用自定义数据源
    String CUSTOM_DATASOURCE = "TEMPLATEJ-CUSTOM-DATASOURCE";
}
