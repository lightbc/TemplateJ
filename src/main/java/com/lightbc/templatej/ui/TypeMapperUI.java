package com.lightbc.templatej.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.Configurable;
import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.config.TemplateJSettings;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.utils.DialogUtil;
import lombok.Data;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

/**
 * 数据类型映射器UI
 */
@Data
public class TypeMapperUI implements Configurable {
    // 映射列表
    private JTable table;
    // 新增映射行按钮
    private JButton add;
    // 删除选择映射行按钮
    private JButton del;
    // UI主面板
    private JPanel mainPanel;
    // 滚动条
    private JScrollPane scrollPane;
    private JPanel operatePanel;
    // 系统层面持久化功能工具
    private TemplateJSettings settings;
    // 表格的数据模型
    private DefaultTableModel tableModel;
    // 模板组名称
    private String groupName;
    // TemplateJ 主UI 对象
    private TemplateJUI templateJUI;
    // JavaType类型映射器数据
    private Object[][] typeMapper;
    // JdbcType类型映射器数据
    private Object[][] jdbcTypeMapper;
    // 映射器类型，0：JavaType映射器，1：JdbcType映射器
    private int mapperType;

    public TypeMapperUI(TemplateJUI templateJUI, int mapperType) {
        this.templateJUI = templateJUI;
        this.mapperType = mapperType;
        this.settings = TemplateJSettings.getInstance();
        this.groupName = templateJUI.getCommonUI().getGroupName();
        this.typeMapper = this.templateJUI.getTemplateUtil().getTemplate(this.groupName).getTypeMapper();
        this.jdbcTypeMapper = this.templateJUI.getTemplateUtil().getTemplate(this.groupName).getJdbcTypeMapper();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initIcons();
        if (mapperType == 0) {
            initJavaTypeTable();
        } else if (mapperType == 1) {
            initJdbcTypeTable();
        }
        operateListener();
    }

    /**
     * 初始化数据类型映射表初始显示内容
     */
    private void initJavaTypeTable() {
        this.typeMapper = this.typeMapper != null ? this.typeMapper : DefaultTemplateParams.getDefaultTableData();
        this.tableModel = new DefaultTableModel(this.typeMapper, DefaultTemplateParams.getDefaultJavaTypeTableHeader());
        initTable();
    }

    /**
     * 初始化JdbcType数据映射表初始显示内容
     */
    private void initJdbcTypeTable() {
        this.jdbcTypeMapper = this.jdbcTypeMapper != null ? this.jdbcTypeMapper : DefaultTemplateParams.getDefaultJdbcTypeTableData();
        this.tableModel = new DefaultTableModel(this.jdbcTypeMapper, DefaultTemplateParams.getDefaultJdbcTypeTableHeader());
        initTable();
    }

    /**
     * 初始化映射表
     */
    private void initTable() {
        this.mainPanel.setLayout(new BorderLayout());
        this.table = new JTable(this.tableModel);
        // 设置行高
        this.table.setRowHeight(24);
        // 设置表格内容整体为左对齐
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        this.table.setDefaultRenderer(Object.class, renderer);
        this.table.getTableHeader().setDefaultRenderer(renderer);
        this.scrollPane = new JScrollPane(this.table);
        this.mainPanel.add(this.scrollPane, BorderLayout.CENTER);
        this.mainPanel.add(this.operatePanel, BorderLayout.EAST);
        this.mainPanel.setVisible(true);
    }

    /**
     * 操作功能事件监听
     */
    private void operateListener() {
        // 新增类型映射事件监听
        this.add.addActionListener(e -> this.tableModel.addRow(new Object[]{"", ""}));
        // 删除选中的类型映射事件监听
        this.del.addActionListener(e -> {
            int sr = this.table.getSelectedRow();
            // 删除选择的行
            if (sr != -1) {
                this.tableModel.removeRow(sr);
            }
        });
    }

    /**
     * 获取编辑后的类型映射表中的数据内容
     *
     * @return Object[][]
     */
    private Object[][] getTableData() {
        Vector vectors = this.tableModel.getDataVector();
        Object[][] objects = new Object[vectors.size()][];
        for (int i = 0; i < vectors.size(); i++) {
            Vector vector = (Vector) vectors.get(i);
            // 数据列类型
            String column = vector.get(0).toString();
            // 数据列对应的java/jdbc类型
            String type = vector.get(1).toString();
            Object[] object = new Object[]{column, type};
            objects[i] = object;
        }
        return objects;
    }

    /**
     * 打开类型映射器
     */
    public void typeMapper() {
        DialogUtil dialogUtil = new DialogUtil();
        String title = "";
        if (this.mapperType == 0) {
            title = Message.JAVA_TYPE_MAPPER_OPEN.getTitle().concat(this.groupName);
        } else if (this.mapperType == 1) {
            title = Message.JDBC_TYPE_MAPPER_OPEN.getTitle().concat(this.groupName);
        }
        int c = dialogUtil.showConfirmDialog(null, getMainPanel(), title);
        // 点击确定按钮，保存当前模板组的最新数据类型映射配置信息
        if (c == 0) {
            apply();
        }
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this.mainPanel;
    }

    @Override
    public boolean isModified() {
        return this.mapperType == 0 ? javaTypeModified() : jdbcTypeModified();
    }

    @Override
    public void apply() {
        if (this.mapperType == 0) {
            javaTypeApply();
        } else if (this.mapperType == 1) {
            jdbcTypeApply();
        }
    }

    /**
     * javaType映射类型表数据修改
     *
     * @return boolean true-修改，false-未修改
     */
    private boolean javaTypeModified() {
        // 判断数据类型映射器中的配置信息是否存在修改，存在修改，启用apply按钮功能
        Object[][] objects = getTableData();
        return !Arrays.deepEquals(this.typeMapper, objects);
    }

    /**
     * jdbcType映射类型表数据修改
     *
     * @return Boolean true-修改，false-未修改
     */
    private boolean jdbcTypeModified() {
        // 判断数据类型映射器中的配置信息是否存在修改，存在修改，启用apply按钮功能
        Object[][] objects = getTableData();
        return !Arrays.deepEquals(this.jdbcTypeMapper, objects);
    }

    /**
     * javaType类型映射变更应用
     */
    private void javaTypeApply() {
        // 当前模板组，数据类型映射，最新配置数据持久化
        Object[][] objects = getTableData();
        this.templateJUI.getTemplateUtil().getTemplate(this.groupName).setTypeMapper(objects);
        this.templateJUI.getSettings().save();
    }

    /**
     * jdbcType类型映射变更应用
     */
    private void jdbcTypeApply() {
        // 当前模板组，数据类型映射，最新配置数据持久化
        Object[][] objects = getTableData();
        this.templateJUI.getTemplateUtil().getTemplate(this.groupName).setJdbcTypeMapper(objects);
        this.templateJUI.getSettings().save();
    }

    /**
     * 初始化按钮显示图标，系统图标（2019.1）
     */
    private void initIcons() {
        // 新增图标
        this.add.setIcon(AllIcons.General.Add);
        // 删除图标
        this.del.setIcon(AllIcons.General.Remove);
    }
}
