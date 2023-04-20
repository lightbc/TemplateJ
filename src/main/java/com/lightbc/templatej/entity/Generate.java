package com.lightbc.templatej.entity;

import lombok.Data;

/**
 * 模板生成，前端传输的主要信息
 */
@Data
public class Generate {
    //模板生成文件保存位置-自定义路径部分
    private String savePath;
    //文件保存名称+扩展名
    private String fileName;
    // xml文件保存位置
    private String xmlSavePath;
}
