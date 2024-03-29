package com.lightbc.templatej.interfaces;

/**
 * 初始配置信息
 */
public interface ConfigInterface {
    // 插件ID
    String PLUGIN_ID = "com.lightbc.templatej";
    // 默认的模板名称
    String DEFAULT_FILENAME = "TemplateJ.java";
    // 模板文件选择器中的默认选项
    String DEFAULT_GROUP_FILE_VALUE = "---请选择模板---";
    // 模板组value
    String GROUP_NAME_VALUE = "Default";
    // 模板组文件value
    String[] GROUP_FILE_VALUE = {"Controller.java", "Entity.java", "Mapper.java", "MapperXML.xml", "Service.java", "ServiceImpl.java"};
    // 拓展名value
    String EXT_VALUE = "ft";
    // 编码格式Value
    String ENCODE_VALUE = "UTF-8";
    // 默认单选按钮选中值
    String DEFAULT_RADIO_GROUP_NAME = "模板组";
    // 全局设置文件的文件类型
    String SETTING_TYPE = ".ftl";
    // 主UI界面显示名称
    String PLUGIN_TEMPLATEJ_DISPLAYNAME = "TemplateJ";
    // 模板生成时候的默认拓展名
    String DEFAULT_GENERATE_EXT = ".java";
    // 默认包名
    String DEFAULT_PACKAGE_NAME = "com.lightbc.templatej";
    // 默认新名称后缀
    String DEFAULT_RENAME_SUFFIX = "-副本";
    // 预览功能，表搜索框占位符
    String TABLE_NAME_SEARCH_PLACEHOLDER = "按回车查询";
    // 插件缓存数据目录
    String PLUGIN_CACHE_DIR = "cache";
    // 插件配置文件名称
    String PLUGIN_CONFIG_FILENAME = "TemplateJ.cfg";
    // 模板导入
    String TEMPLATE_IMPORT = "模板导入";
    // 模板导出
    String TEMPLATE_EXPORT = "模板导出";
    // 插件默认文件拓展名
    String PLUGIN_DEFAULT_EXT = ".tj";
    // 插件默认文件拓展名，无点
    String PLUGIN_DEFAULT_EXT_WITH_NO_DOT = "tj";
    // 自定义数源文件允许的文件类型
    String CUSTOM_DATASOURCE_FILE_TYPE = "json";
    // API文档文件类型
    String API_DOC_EXT = ".html";
    // API文档文件类型，无点
    String API_DOC_EXT_WITH_NO_DOT = "html";
    // pdf
    String PDF = ".pdf";
    // 图片缓存目录
    String IMAGES_CACHE_DIR = "images";
    // 前端传参对象key
    String GENERATE_KEY_NAME = "generate";
    // 日志
    String PLUGIN_LOGGING = "日志记录";
}
