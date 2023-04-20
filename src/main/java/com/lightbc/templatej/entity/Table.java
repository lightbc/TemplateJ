package com.lightbc.templatej.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据表的主要信息
 */
@Data
public class Table {
    //数据库名
    private String schemaName;
    //原表名
    private String originName;
    //表名
    private String name;
    //表注释
    private String comment;
    //表主键列名称
    private String originPrimaryKey;
    //java 字段名称（处理后）
    private String primaryKey;
    //字段信息
    private List<ColumnInfo> columns;
    //所有字段名
    private List<String> columnNames;
    //字段+类型Map
    private Map<String, Object> columnType;
}
