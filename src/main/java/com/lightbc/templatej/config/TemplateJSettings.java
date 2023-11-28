package com.lightbc.templatej.config;

import com.alibaba.fastjson.JSONObject;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.FileUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 插件程序的数据持久化组件
 */
@Data
public class TemplateJSettings {
    private List<Template> templates;
    private String selectGroup;
    private String selectGroupFile;
    private static TemplateJSettings settings;


    public TemplateJSettings() {
        initTemplate();
    }

    /**
     * 获取持久化组件的实例
     *
     * @return TemplateJSettings
     */
    public static TemplateJSettings getInstance() {
        if (settings == null) {
            TemplateJSettings tjs = load();
            settings = tjs == null ? new TemplateJSettings() : tjs;
        }
        return settings;
    }


    /**
     * 初始化模板
     */
    private void initTemplate() {
        this.templates = DefaultTemplateParams.getDefaultTemplates();
    }

    /**
     * 保存配置数据
     */
    public synchronized void save() {
        FileUtil fileUtil = new FileUtil();
        String cfgPath = fileUtil.getPluginConfigFilePath();
        String cfg = JSONObject.toJSONString(this);
        fileUtil.write(cfgPath, cfg);
    }

    /**
     * 加载配置数据
     *
     * @return 配置数据对象
     */
    private synchronized static TemplateJSettings load() {
        FileUtil fileUtil = new FileUtil();
        String cfgPath = fileUtil.getPluginConfigFilePath();
        if (StringUtils.isBlank(cfgPath)) {
            return null;
        }
        String cfg = fileUtil.read(cfgPath);
        if (StringUtils.isBlank(cfg)) {
            return null;
        }
        return JSONObject.parseObject(cfg, TemplateJSettings.class);
    }

}
