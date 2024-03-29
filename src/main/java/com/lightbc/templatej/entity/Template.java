package com.lightbc.templatej.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Template {
    // 模板组名称
    private String groupName;
    // 模板组模板文件名称
    private List<String> groupFiles;
    // 模板组模板文件模板内容
    private Map<String, String> fileContentMap;
    // 模板组全局配置信息
    private String globalConfig;
    // JavaType类型映射器，以模板组为单位的个性化映射配置
    private Object[][] typeMapper;
    // JdbcType类型映射器
    private Object[][] jdbcTypeMapper;
    // API接口文档模板
    private String apiDoc;
}
