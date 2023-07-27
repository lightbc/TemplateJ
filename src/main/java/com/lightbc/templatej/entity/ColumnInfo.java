package com.lightbc.templatej.entity;

import lombok.Data;

/**
 * 数据表单列主要信息
 */
@Data
public class ColumnInfo {
    //原表列名称
    private String originColumnName;
    //Java 字段名称（处理后）
    private String columnName;
    //JavaType数据类型
    private String dataType;
    //字符编码
    private String charset;
    //字段备注信息
    private String columnComment;
    //Java类型
    private String javaType;
    //JdbcType数据类型
    private String jdbcType;
}
