package com.lightbc.templatej.enums;

/**
 * 消息提示
 */
public enum Message {
    // 配置标题和消息内容
    NO_CHOICE_GENERATE_TEMPLATE("未选择", "请选择需要生成的模板！"),
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
    RESET_TEMPLATEJ("重置", "是否确认重置？重置后你的自定义配置信息将会被移除！"),

    // 配置标题
    ADD_TEMPLATE("新增模板"),
    COPY_TEMPLATE("复制模板"),
    DEL_TEMPLATE("删除选择模板组/模板文件"),
    GLOBAL_CONFIG("配置项-"),
    PREVIEW_TEMPLATE("效果预览"),
    PACKAGE_CHOOSER_TITLE("包路径选择"),
    TYPE_MAPPER_OPEN("数据类型映射-"),
    HELP("帮助");


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
