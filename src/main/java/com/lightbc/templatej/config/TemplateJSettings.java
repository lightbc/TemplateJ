package com.lightbc.templatej.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.entity.Template;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 插件程序的数据持久化组件
 */
@Data
@State(name = "TemplateJSettings", storages = {@Storage("TemplateJ-Settings.xml")})
public class TemplateJSettings implements PersistentStateComponent<TemplateJSettings> {
    private List<Template> templates;
    private String selectGroup;
    private String selectGroupFile;

    public TemplateJSettings() {
        init();
    }

    /**
     * 获取持久化组件的实例
     *
     * @return TemplateJSettings
     */
    public static TemplateJSettings getInstance() {
        return ServiceManager.getService(TemplateJSettings.class);
    }

    /**
     * 初始化
     */
    private void init() {
        initTemplate();
    }

    /**
     * 初始化模板
     */
    private void initTemplate() {
        this.templates = DefaultTemplateParams.getDefaultTemplates();
    }

    @Nullable
    @Override
    public TemplateJSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TemplateJSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }

}
