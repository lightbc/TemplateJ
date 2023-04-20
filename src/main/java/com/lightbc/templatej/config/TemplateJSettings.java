package com.lightbc.templatej.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.ConfigInterface;
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
    // 默认类型选择器名称
    public static final String DEFAULT_NAME = ConfigInterface.GROUP_NAME_VALUE;
    // 编辑器编辑内容
    private String content = "";
    // 类型映射器表头
    private Object[] tableHeader;
    // 类型映射器默认映射数据
    private Object[][] defaultTableData;
    // 类型映射器，以模板组为单位的个性化映射配置
    private Map<String, Object[][]> typeMapper;
    private List<Template> templates;
    private String selectGroupName = DEFAULT_NAME;
    private Object selectGroupFile = 0;

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
    public void init() {
        initTemplate();
        initTypeMapper();
    }

    /**
     * 初始化映射器
     */
    public void initTypeMapper() {
        tableHeader = new Object[]{"columnType", "javaType"};
        defaultTableData= DefaultTemplateParams.getDefaultTableData();
        if (typeMapper == null) {
            typeMapper=DefaultTemplateParams.getDefaultTypeMapper();
        }
    }

    /**
     * 初始化模板
     */
    public void initTemplate() {
        this.templates=DefaultTemplateParams.getDefaultTemplates();
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
