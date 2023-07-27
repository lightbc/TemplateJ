package com.lightbc.templatej;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.interfaces.ConfigInterface;

import java.util.*;

public class DefaultTemplateParams {
    public static final String DEFAULT_NAME = ConfigInterface.GROUP_NAME_VALUE;
    private static final String GLOBAL_CONFIG = getDefaultGlobalConfig();

    /**
     * 获取默认模板数据
     *
     * @return map
     */
    private static Map<String, String> getDefaultTemplateData() {
        Map<String, String> map = new HashMap<>();
        String controller = "<#assign savePath=generate.setSavePath(\"/controller\")>\n" +
                "<#assign fileName=\"${tableName}Controller\">\n" +
                "${generate.setFileName(\"${fileName}\")}\n" +
                "<#assign serviceBean=tools.toLowerCamelCase(\"${serviceName}\")>\n" +
                "        \n" +
                "package ${controllerPackagePath};\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "import org.springframework.web.bind.annotation.RequestMethod;\n" +
                "import ${entityPackagePath}.${tableName};\n" +
                "import ${servicePackagePath}.${serviceName};\n" +
                "\n" +
                "/**\n" +
                "*\n" +
                "* @Description ${tableName} table controller\n" +
                "* @author ${author}\n" +
                "* @Date ${date}\n" +
                "* @since 1.0\n" +
                "*\n" +
                "*/\n" +
                "@RestController\n" +
                "@RequestMapping(\"${tableName}\")\n" +
                "public class ${fileName} {\n" +
                "    @Autowired\n" +
                "    private ${serviceName} ${serviceBean};\n" +
                "\n" +
                "    /**\n" +
                "    * 单条数据插入\n" +
                "    * @param add 单条数据\n" +
                "    */\n" +
                "    @RequestMapping(value=\"/add\",method=RequestMethod.POST)\n" +
                "    public void insert(${tableName} add){\n" +
                "        ${serviceBean}.insert(add);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键删除数据\n" +
                "    * @param id 主键\n" +
                "    */\n" +
                "    @RequestMapping(value=\"/del\",method=RequestMethod.GET)\n" +
                "    public void delete(String id){\n" +
                "        ${serviceBean}.delete(id);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键更新数据\n" +
                "    * @param update 主键\n" +
                "    */\n" +
                "    @RequestMapping(value=\"/update\",method=RequestMethod.POST)\n" +
                "    public void update(${tableName} update){\n" +
                "        ${serviceBean}.update(update);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键查询数据\n" +
                "    * @param id 主键\n" +
                "    * @return ${tableName} 数据对象\n" +
                "    */\n" +
                "    @RequestMapping(value=\"/query/u\",method=RequestMethod.GET)\n" +
                "    public ${tableName} queryById(String id){\n" +
                "        return ${serviceBean}.queryById(id);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 查询多条记录\n" +
                "    * @param query 查询条件\n" +
                "    * @return List<${tableName}> 查询记录集\n" +
                "    */\n" +
                "    @RequestMapping(value=\"/query\",method=RequestMethod.GET)\n" +
                "    public List<${tableName}> query(${tableName} query){\n" +
                "        return ${serviceBean}.query(query);\n" +
                "    }\n" +
                "}";
        String entity = "<#assign fileName=\"${tableName}\">\n" +
                "<#assign columns=table.getColumns()>\n" +
                "<#assign savePath=generate.setSavePath(\"/entity\")>\n" +
                "${generate.setFileName(\"${fileName}\")}\n" +
                "\n" +
                "package ${entityPackagePath};\n" +
                "\n" +
                "import java.util.Date;\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * @Description ${tableName} table entity\n" +
                " * @author ${author}\n" +
                " * @Date ${date}\n" +
                " * @since 1.0\n" +
                " *\n" +
                " */\n" +
                "public class ${fileName} implements Serializable{\n" +
                "    private static final long serialVersionUID = ${tools.serial()};\n" +
                "\n" +
                "    <#list columns as column>\n" +
                "    // ${column.columnComment!}\n" +
                "    private ${column.javaType} ${tools.toLowerCamelCase(\"${column.columnName}\")};\n" +
                "    </#list>\n" +
                "\n" +
                "    <#list columns as column>\n" +
                "    <@entityMethod methodName=\"${column.columnName}\" javaType=\"${column.javaType}\"/>\n" +
                "    </#list>\n" +
                "\n" +
                "}\n" +
                "\n" +
                "<#macro entityMethod methodName javaType>\n" +
                "    public ${javaType} ${tools.append(\"get\",\"${methodName}\")} (){\n" +
                "        return ${tools.toLowerCamelCase(\"${methodName}\")};\n" +
                "    }\n" +
                "\n" +
                "    public void ${tools.append(\"set\",\"${methodName}\")} (${javaType} ${tools.toLowerCamelCase(\"${methodName}\")}){\n" +
                "        this.${tools.toLowerCamelCase(\"${methodName}\")}=${tools.toLowerCamelCase(\"${methodName}\")};\n" +
                "    }\n" +
                "\n" +
                "</#macro>";
        String mapper = "<#assign fileName=\"${tableName}Mapper\">\n" +
                "<#assign savePath=generate.setSavePath(\"/dao\")>\n" +
                "${generate.setFileName(\"${fileName}\")}\n" +
                "package ${daoPackagePath};\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import ${entityPackagePath}.${tableName};\n" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * @Description ${tableName} table dao\n" +
                " * @author ${author}\n" +
                " * @Date ${date}\n" +
                " * @since 1.0\n" +
                " *\n" +
                " */\n" +
                "public interface ${fileName}{\n" +
                "\n" +
                "    /**\n" +
                "    * 单条数据插入\n" +
                "    * @param add 单条数据\n" +
                "    */\n" +
                "    int insert(${tableName} add);\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键删除数据\n" +
                "    * @param id 主键\n" +
                "    */\n" +
                "    int delete(String id);\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键更新数据\n" +
                "    * @param update 主键\n" +
                "    */\n" +
                "    int update(${tableName} update);\n" +
                "\n" +
                "    /**\n" +
                "     * 根据主键查询数据\n" +
                "     * @param id 主键\n" +
                "     * @return ${tableName} 数据对象\n" +
                "     */\n" +
                "    ${tableName} queryById(String id);\n" +
                "\n" +
                "    /**\n" +
                "     * 查询多条记录\n" +
                "     * @param query 查询条件\n" +
                "     * @return List<${tableName}> 查询记录集\n" +
                "     */\n" +
                "    List<${tableName}> query(${tableName} query);\n" +
                "}";
        String mapperXml = "<#assign columns=table.getColumns()>\n" +
                "<#assign xmlSavePath=tools.append(\"${rootPath}\",\"/src/main/resources/mappers\")>\n" +
                "${generate.setXmlSavePath(\"${xmlSavePath}\")}\n" +
                "${generate.setFileName(\"${mapperName}\")}\n" +
                "\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"${daoPackagePath}.${mapperName}\">\n" +
                "\n" +
                "    <!-- 查询结果集映射关系 -->\n" +
                "    <resultMap type=\"${entityPackagePath}.${tableName}\" id=\"${tableName}ResultMap\">\n" +
                "        <@resultMap/>\n" +
                "    </resultMap>\n" +
                "\n" +
                "    <!-- 数据列 -->\n" +
                "    <sql id=\"base_cols\">\n" +
                "        <#list columns as column>\n" +
                "        <#if columns?size-1=column_index>\n" +
                "        ${column.originColumnName}\n" +
                "        <#else>\n" +
                "        ${tools.append(\"${column.originColumnName}\",\",\")}\n" +
                "        </#if>\n" +
                "        </#list>\n" +
                "    </sql>\n" +
                "\n" +
                "    <!-- 主键查询-->\n" +
                "    <sql id=\"base_where_id\">\n" +
                "        <trim prefix=\"where\" prefixOverrides=\"and | or\">\n" +
                "        <#if pk!=\"\" && pkType==\"String\">\n" +
                "            <if test=\"${pk} != null and ${pk} != ''\">\n" +
                "                and ${opk} = ${tools.append(\"#{\",\"${pk}\",\"}\")}\n" +
                "            </if>\n" +
                "        <#else>\n" +
                "            <if test=\"${pk} != null\">\n" +
                "                and ${opk} = ${tools.append(\"#{\",\"${pk}\",\"}\")}\n" +
                "            </if>\n" +
                "        </#if>\n" +
                "        </trim>\n" +
                "    </sql>\n" +
                "\n" +
                "    <!-- 查询条件 -->\n" +
                "    <sql id=\"base_where\">\n" +
                "        <trim prefix=\"where\" prefixOverrides=\"and | or\">\n" +
                "        <#list columns as column>\n" +
                "        <#if  pk!=\"\" && pkType==\"String\">\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null and ${tools.toLowerCamelCase(\"${column.columnName}\")} != ''\">\n" +
                "                and ${column.originColumnName} = ${tools.append(\"#{\",tools.toLowerCamelCase(\"${column.columnName}\"),\"}\")}\n" +
                "            </if>\n" +
                "        <#else>\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null\">\n" +
                "                and ${column.originColumnName} = ${tools.append(\"#{\",tools.toLowerCamelCase(\"${column.columnName}\"),\"}\")}\n" +
                "            </if>\n" +
                "        </#if>\n" +
                "        </#list>\n" +
                "        </trim>\n" +
                "    </sql>\n" +
                "\n" +
                "    <!-- 插入数据 -->\n" +
                "    <insert id=\"insert\">\n" +
                "        insert into ${table.getSchemaName()}.${table.getOriginName()}\n" +
                "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <#list columns as column>\n" +
                "        <#if  pk!=\"\" && pkType==\"String\">\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null and ${tools.toLowerCamelCase(\"${column.columnName}\")} != ''\">\n" +
                "                ${column.originColumnName},\n" +
                "            </if>\n" +
                "        <#else>\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null\">\n" +
                "                ${column.originColumnName},\n" +
                "            </if>\n" +
                "        </#if>\n" +
                "        </#list>\n" +
                "        </trim>\n" +
                "        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <#list columns as column>\n" +
                "        <#if  pk!=\"\" && pkType==\"String\">\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null and ${tools.toLowerCamelCase(\"${column.columnName}\")} != ''\">\n" +
                "                ${tools.append(\"#{\",tools.toLowerCamelCase(\"${column.columnName}\"),\"}\")},\n" +
                "            </if>\n" +
                "        <#else>\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null\">\n" +
                "                ${tools.append(\"#{\",tools.toLowerCamelCase(\"${column.columnName}\"),\"}\")},\n" +
                "            </if>\n" +
                "        </#if>\n" +
                "        </#list>\n" +
                "        </trim>\n" +
                "    </insert>\n" +
                "\n" +
                "    <!-- 删除数据 -->\n" +
                "    <delete id=\"delete\">\n" +
                "        delete from  ${table.getSchemaName()}.${table.getOriginName()}\n" +
                "        <include refid=\"base_where_id\"/>\n" +
                "    </delete>\n" +
                "\n" +
                "    <!-- 更新数据 -->\n" +
                "    <update id=\"update\">\n" +
                "        update ${table.getSchemaName()}.${table.getOriginName()}\n" +
                "        <trim prefix=\"set\" suffixOverrides=\",\">\n" +
                "        <#list columns as column>\n" +
                "        <#if  opk!=column.originColumnName>\n" +
                "            <if test=\"${tools.toLowerCamelCase(\"${column.columnName}\")} != null\">\n" +
                "                ${column.originColumnName}=${tools.append(\"#{\",tools.toLowerCamelCase(\"${column.columnName}\"),\"}\")},\n" +
                "            </if>\n" +
                "        </#if>\n" +
                "        </#list>\n" +
                "        </trim>\n" +
                "        <include refid=\"base_where_id\"/>\n" +
                "    </update>\n" +
                "\n" +
                "    <!-- 单条数据查询 -->\n" +
                "    <select id=\"queryById\" resultMap=\"${tableName}ResultMap\">\n" +
                "        select\n" +
                "        <include refid=\"base_cols\"/>\n" +
                "        from ${table.getSchemaName()}.${table.getOriginName()}\n" +
                "        <include refid=\"base_where_id\"/>\n" +
                "    </select>\n" +
                "\n" +
                "    <!-- 多条件结果集查询 -->\n" +
                "    <select id=\"query\" resultMap=\"${tableName}ResultMap\">\n" +
                "        select\n" +
                "        <include refid=\"base_cols\"/>\n" +
                "        from ${table.getSchemaName()}.${table.getOriginName()}\n" +
                "        <include refid=\"base_where\"/>\n" +
                "    </select>\n" +
                "\n" +
                "</mapper>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<#-- 处理MySQL Xml文件结果映射集 -->\n" +
                "<#macro resultMap>\n" +
                "    <#list table.getColumns() as column>\n" +
                "        <result property=\"${tools.toLowerCamelCase(\"${column.columnName}\")}\" column=\"${column.originColumnName}\" jdbcType=\"<@jdbcType column.jdbcType/>\"/>\n" +
                "    </#list>\n" +
                "</#macro>\n" +
                "\n" +
                "<#macro jdbcType jdbcType>\n" +
                "<#-- 压缩成一行，移除字符串两端空字符显示 -->\n" +
                "<@compress single_line=true>\n" +
                "    ${tools.toUpperCase(jdbcType)}\n" +
                "</@compress>\n" +
                "</#macro>";
        String service = "<#assign fileName=\"${tableName}Service\">\n" +
                "<#assign savePath=generate.setSavePath(\"/service\")>\n" +
                "${generate.setFileName(\"${fileName}\")}\n" +
                "\n" +
                "package ${servicePackagePath};\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import ${entityPackagePath}.${tableName};\n" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * @Description ${tableName} table service\n" +
                " * @author ${author}\n" +
                " * @Date ${date}\n" +
                " * @since 1.0\n" +
                " *\n" +
                " */\n" +
                "public interface ${fileName}{\n" +
                "\n" +
                "    /**\n" +
                "    * 单条数据插入\n" +
                "    * @param add 单条数据\n" +
                "    */\n" +
                "    int insert(${tableName} add);\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键删除数据\n" +
                "    * @param id 主键\n" +
                "    */\n" +
                "    int delete(String id);\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键更新数据\n" +
                "    * @param update 主键\n" +
                "    */\n" +
                "    int update(${tableName} update);\n" +
                "\n" +
                "    /**\n" +
                "     * 根据主键查询数据\n" +
                "     * @param id 主键\n" +
                "     * @return ${tableName} 数据对象\n" +
                "     */\n" +
                "    ${tableName} queryById(String id);\n" +
                "\n" +
                "    /**\n" +
                "     * 查询多条记录\n" +
                "     * @param query 查询条件\n" +
                "     * @return List<${tableName}> 查询记录集\n" +
                "     */\n" +
                "    List<${tableName}> query(${tableName} query);\n" +
                "}";
        String serviceImpl = "<#assign fileName=\"${tableName}ServiceImpl\">\n" +
                "<#assign savePath=generate.setSavePath(\"/service/impl\")>\n" +
                "${generate.setFileName(\"${fileName}\")}\n" +
                "<#assign mapperBean=tools.toLowerCamelCase(\"${mapperName}\")>\n" +
                "package ${serviceImplPackagePath};\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import ${servicePackagePath}.${serviceName};\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import ${daoPackagePath}.${mapperName};\n" +
                "import ${entityPackagePath}.${tableName};\n" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * @Description ${tableName} table service implement\n" +
                " * @author ${author}\n" +
                " * @Date ${date}\n" +
                " * @since 1.0\n" +
                " *\n" +
                " */\n" +
                "@Service(\"${serviceName}\")\n" +
                "public class ${fileName} implements ${serviceName}{\n" +
                "    @Autowired\n" +
                "    private ${mapperName} ${mapperBean};\n" +
                "\n" +
                "    /**\n" +
                "    * 单条数据插入\n" +
                "    * @param add 单条数据\n" +
                "    */\n" +
                "    public int insert(${tableName} add){\n" +
                "        return ${mapperBean}.insert(add);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键删除数据\n" +
                "    * @param id 主键\n" +
                "    */\n" +
                "    public int delete(String id){\n" +
                "        return ${mapperBean}.delete(id);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "    * 根据主键更新数据\n" +
                "    * @param update 主键\n" +
                "    */\n" +
                "    public int update(${tableName} update){\n" +
                "        return ${mapperBean}.update(update);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 根据主键查询数据\n" +
                "     * @param id 主键\n" +
                "     * @return ${tableName} 数据对象\n" +
                "     */\n" +
                "    public ${tableName} queryById(String id){\n" +
                "        return ${mapperBean}.queryById(id);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 查询多条记录\n" +
                "     * @param query 查询条件\n" +
                "     * @return List<${tableName}> 查询记录集\n" +
                "     */\n" +
                "    public List<${tableName}> query(${tableName} query){\n" +
                "        return ${mapperBean}.query(query);\n" +
                "    }\n" +
                "}";

        map.put("Controller.java", controller);
        map.put("Entity.java", entity);
        map.put("Mapper.java", mapper);
        map.put("MapperXML.xml", mapperXml);
        map.put("Service.java", service);
        map.put("ServiceImpl.java", serviceImpl);
        return map;
    }

    private static String getDefaultGlobalConfig() {
        return "<#assign author=\"lightbc\">\n" +
                "<#assign date=tools.getDate(\"yyyy-MM-dd HH:mm:ss\")>\n" +
                "\n" +
                "<#assign serviceName=\"${tableName}Service\">\n" +
                "<#assign mapperName=\"${tableName}Mapper\">\n" +
                "<#assign serviceImplName=\"${tableName}ServiceImpl\">\n" +
                "\n" +
                "<#-- 主键处理 -->\n" +
                "<#assign opk=table.getOriginPrimaryKey()!\"\">\n" +
                "<#assign pk=tools.toLowerCamelCase(table.getPrimaryKey()!\"\")>\n" +
                "<#assign pkType=table.getColumnType()[\"${pk}\"]!\"\">\n" +
                "    \n" +
                "<#-- 引用包路径 -->\n" +
                "<#assign controllerPackagePath=tools.append(\"${packageName}\",\".controller\")>\n" +
                "<#assign daoPackagePath=tools.append(\"${packageName}\",\".dao\")>\n" +
                "<#assign entityPackagePath=tools.append(\"${packageName}\",\".entity\")>\n" +
                "<#assign servicePackagePath=tools.append(\"${packageName}\",\".service\")>\n" +
                "<#assign serviceImplPackagePath=tools.append(\"${packageName}\",\".service.impl\")>";
    }


    /**
     * 获取默认模板
     *
     * @return 模板数组
     */
    public static List<Template> getDefaultTemplates() {
        List<Template> templates = new ArrayList<>();
        // 默认模板对象
        Template template = new Template();
        // 默认模板组名称
        template.setGroupName(DEFAULT_NAME);
        // 默认模板文件
        String[] groupFiles = ConfigInterface.GROUP_FILE_VALUE;
        // 数据类型转换
        List<String> templateFiles = new ArrayList<>(Arrays.asList(groupFiles));
        template.setGroupFiles(templateFiles);
        // 默认文件映射的默认内容
        template.setFileContentMap(getDefaultTemplateData());
        // 默认模板组的全局配置
        template.setGlobalConfig(GLOBAL_CONFIG);
        // 默认模板组数据类型映射集
        template.setTypeMapper(getDefaultTableData());
        // 默认JdbcType类型映射集
        template.setJdbcTypeMapper(getDefaultJdbcTypeTableData());
        templates.add(template);
        return templates;
    }

    public static Object[] getDefaultJavaTypeTableHeader() {
        return new Object[]{"ColumnType", "JavaType"};
    }

    public static Object[] getDefaultJdbcTypeTableHeader() {
        return new Object[]{"ColumnType", "JdbcType"};
    }

    /**
     * 获取默认映射表数据
     *
     * @return object
     */
    public static Object[][] getDefaultTableData() {
        return new Object[][]{
                new Object[]{"varchar", "java.lang.String"},
                new Object[]{"char", "java.lang.String"},
                new Object[]{"text", "java.lang.String"},
                new Object[]{"decimal", "java.lang.Double"},
                new Object[]{"integer", "java.lang.Integer"},
                new Object[]{"int", "java.lang.Integer"},
                new Object[]{"datetime", "java.util.Date"},
                new Object[]{"timestamp", "java.util.Date"},
                new Object[]{"boolean", "java.lang.Boolean"},
                new Object[]{"date", "java.util.Date"}
        };
    }

    public static Object[][] getDefaultJdbcTypeTableData() {
        return new Object[][]{
                new Object[]{"tinyint", "tinyint"},
                new Object[]{"smallint", "smallint"},
                new Object[]{"int", "integer"},
                new Object[]{"integer", "integer"},
                new Object[]{"bigint", "bigint"},
                new Object[]{"bit", "bit"},
                new Object[]{"real", "real"},
                new Object[]{"double", "double"},
                new Object[]{"float", "float"},
                new Object[]{"decimal", "decimal"},
                new Object[]{"numeric", "numeric"},
                new Object[]{"char", "char"},
                new Object[]{"varchar", "varchar"},
                new Object[]{"date", "date"},
                new Object[]{"time", "time"},
                new Object[]{"timestamp", "timestamp"},
                new Object[]{"datetime", "datetime"},
                new Object[]{"blob", "blob"},
                new Object[]{"tinytext", "longvarchar"},
                new Object[]{"text", "longvarchar"},
                new Object[]{"longtext", "longvarchar"},
                new Object[]{"binary", "binary"},
                new Object[]{"varbinary", "varbinary"}
        };
    }
}
