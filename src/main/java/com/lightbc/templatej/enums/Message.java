package com.lightbc.templatej.enums;

/**
 * 消息提示
 */
public enum Message {
    // 配置标题和消息内容
    NO_CHOICE_GENERATE_TEMPLATE("未选择", "请选择需要生成的模板！"),
    NO_CONFIG_MODULE("警告", "未获取到模块配置信息！"),
    ADD_CHOICE_FAIL("选择失败", "新增模板组/模板文件名称不能为空！"),
    ADD_EXIST("文件新增", "新增文件已存在！请修改文件名后再试！"),
    DEL_DEFAULT_TEMPLATE_FAIL("删除失败", "无法对默认的模板组/模板文件进行删除操作！！！"),
    DEL_CONTINUE("文件删除", "确认继续将删除文件！！！"),
    PREVIEW_CHOICE_TIP("提示", "请选择预览文件"),
    DIALOG_LOAD_ERROR("加载错误", "对话框加载失败！"),
    FILE_EXIST("文件重复", "文件已存在!"),
    TEMPLATE_REPEAT_CREATE("重复创建", "文件【%s】已存在，是否覆盖原文件？"),
    GENERATE_ERROR("生成错误", "模板【%s】生成错误，错误信息【%s】"),
    DATABASE_NOT_CONNECT("错误", "获取已连接数据库失败！"),
    TEMPLATE_CONTENT_EMPTY("提示", "模板内容不能为空！"),
    GLOBAL_CONFIG_EMPTY("提示", "模板组全局配置为空！"),
    API_DOC_EMPTY("提示", "API文档内容为空！"),
    RESET_TEMPLATEJ("重置", "是否确认重置？重置后你的自定义配置信息将会被移除！"),
    RENAME_IS_BLANK("提示", "新名称不能为空！"),
    RENAME_GROUP_EXIST("提示", "此名称的模板组已存在！"),
    RENAME_GROUP_FILE_EXIST("提示", "此名称的模板文件已存在！"),
    RENAME_DEFAULT_TEMPLATE_FAIL("重命名错误", "无法对默认的模板组/模板文件进行重命名操作！！！"),
    OPERATE_TEMPLATE_NOT_EXIST("警告", "操作的模板对象不存在！"),
    NO_SELECT_GROUP("提示", "未选择模板组！"),
    NO_SELECT_TEMPLATE_FILE("提示", "未选择需要导出的模板文件！"),
    NO_SELECT_EXPORT_PATH("提示", "模板导出路径为空！"),
    EXPORT_SUCCESS("提示", "模板导出成功！"),
    EXPORT_ERROR("错误", "导出错误："),
    EXPORT_CONTENT_EMPTY("提示", "导出内容为空!"),
    NO_SELECT_IMPORT_PATH("提示", "请选择需要导入的模板路径（文件夹/文件）！"),
    NO_DEFAULT_GROUP_NAME("提示", "请输入默认的模板组名称！"),
    NO_SELECT_IMPORT_TEMPLATE_FILE("提示", "未选择需要导入的模板文件！"),
    IMPORT_SUCCESS("提示", "模板导入成功！"),
    IMPORT_GROUP_EXIST("提示", "导入的模板组已经存在，请修改模板组名称后重试！"),
    IMPORT_TEMPLATE_FILE_NO_EXIST("提示", "选择的文件无效/文件夹目录下无有效的文件存在！"),
    IMPORT_IS_NOT_GLOBAL_CONFIG("提示", "选择的文件非指定格式（格式：模板组名称.tj）！"),
    ERROR_URL("网址打开错误", "尝试了【%s】次后，我选择了摆烂..."),
    FILENAME_EMPTY("警告", "文件名称为空！"),
    EXPORT_PATH_EMPTY("警告", "保存路径为空！"),
    MODULE_PATH_LOAD_ERROR("警告", "选择模块目录未加载到！"),
    CUSTOM_DATASOURCE_EMPTY("警告", "自定义数据源内容为空/指定格式不符合规范！"),
    CUSTOM_DATASOURCE_IMPORT_PATH_WARN("警告", "数源路径为空/无法解析！"),
    API_DOC_PREVIEW_ERROR("错误", "API文档效果预览出错，无法自动打开！"),
    CHANGE_DIR_ERROR("错误", "目录切换失败！"),

    // 配置标题
    ADD_TEMPLATE("新增模板"),
    COPY_TEMPLATE("复制模板"),
    DEL_TEMPLATE("删除选择模板组/模板文件"),
    API_DCO("API文档-"),
    GLOBAL_CONFIG("配置项-"),
    PREVIEW_TEMPLATE("效果预览"),
    PACKAGE_CHOOSER_TITLE("包路径选择"),
    JAVA_TYPE_MAPPER_OPEN("JavaType数据类型映射-"),
    JDBC_TYPE_MAPPER_OPEN("JdbcType数据类型映射-"),
    HELP("帮助"),
    CACHE_ERROR("缓存失败"),
    RENAME_TITLE("重命名（模板组/模板文件）名称"),
    EXPORT("导出");


    // 消息标题
    private String title;
    // 消息内容
    private String msg;

    Message(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    Message(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }
}
