package com.lightbc.templatej.utils;

import com.intellij.database.model.*;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.containers.JBIterable;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库工具类
 */
public class DataBaseUtil {
    // 数据服务提供者（mysql、oracle等）
    @Getter
    @Setter
    private String provider;
    private static Project project;
    // 获取到的数据源信息
    @Getter
    @Setter
    private Map<String, DbDataSource> dataSourceMap = new HashMap<>();

    public DataBaseUtil(){
        init();
    }

    public void init(){
        project = ProjectUtil.getProject();
    }

    /**
     * 获取当前打开项目已连接的数据源名称
     *
     * @return List<String>
     */
    public List<String> getDataSourceNames() {
        List<String> list = new ArrayList<>();
        JBIterable<DbDataSource> dataSources = DbUtil.getDataSources(project);
        if (dataSources != null && dataSources.size() > 0) {
            for (DbDataSource dataSource : dataSources) {
                String name = dataSource.getName();
                list.add(name);
                dataSourceMap.put(name, dataSource);
            }
        }
        return list;
    }

    /**
     * 根据数据源下标获取数据源，并获取该数据原下的数据表信息
     *
     * @param index 连接的数据源下标
     * @return Map<String, List < String>>
     */
    public Map<String, List<String>> getDbTableMap(int index) {
        Map<String, List<String>> map = new HashMap<>();
        DbDataSource dataSource = DbUtil.getDataSources(project).get(index);
        // 数据源的元数据信息
        MetaModel metaModel = dataSource.getModel().getMetaModel();
        // 数据服务提供者信息（低版本可能不支持）
        this.provider = metaModel.getDbms().toString();
        JBIterable<? extends DasObject> modelRoots = dataSource.getModel().getModelRoots();
        for (DasObject dasObject : modelRoots) {
            String schema = dasObject.getName();
            // 判断对象种类是否是数据库类型
            if (dasObject.getKind() == ObjectKind.SCHEMA) {
                List<String> list = new ArrayList<>();
                // 获取当前库下的所有表信息
                JBIterable<? extends DasObject> tables = dasObject.getDasChildren(ObjectKind.TABLE);
                for (DasObject table : tables) {
                    String tableName = table.getName();
                    list.add(tableName);
                }
                // 获取有效的库表信息
                if (list.size() > 0) {
                    map.put(schema, list);
                }
            }
        }
        return map;
    }

    /**
     * 获取DataBase连接的数据源的指定表DbTable对象
     *
     * @param dataSource 数据源
     * @param tableName  表名
     * @return DbTable
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public DbTable getTable(DbDataSource dataSource, String tableName) throws InvocationTargetException, IllegalAccessException {
        // 通过环境的反射工具类，获取指定类中的指定方法
        Method method = ReflectionUtil.getMethod(DbPsiFacade.class, "findElement", DasObject.class);
        DasTable table = getDasTable(dataSource, tableName);
        return (DbTable) method.invoke(DbPsiFacade.getInstance(project), table);
    }

    /**
     * 获取DataBase连接的数据源的指定表DasTable对象
     *
     * @param dataSource 数据源
     * @param tableName  表名
     * @return DasTable
     */
    public DasTable getDasTable(DbDataSource dataSource, String tableName) {
        for (DasTable table : DasUtil.getTables(dataSource)) {
            if (ObjectKind.TABLE.equals(table.getKind())) {
                String dasTableName = table.getName();
                if (tableName.equals(dasTableName)) {
                    return table;
                }
            }
        }
        return null;
    }
}
