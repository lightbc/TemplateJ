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
    // 类型映射器数据
    private Object[][] typeMapper;

    public TypeMapperUI(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        this.settings = TemplateJSettings.getInstance();
        this.groupName = templateJUI.getCommonUI().getGroupName();
        this.typeMapper = this.templateJUI.getTemplateUtil().getTemplate(this.groupName).getTypeMapper();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initIcons();
        initTable();
        operateListener();
    }

    /**
     * 初始化数据类型映射表初始显示内容
     */
    @SuppressWarnings({"BoundFieldAssignment", "UndesirableClassUsage"})
    private void initTable() {
        this.typeMapper = this.typeMapper != null ? this.typeMapper : DefaultTemplateParams.getDefaultTableData();
        this.tableModel = new DefaultTableModel(this.typeMapper, DefaultTemplateParams.getDefaultTableHeader());
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
     * 初始化按钮显示图标，系统图标（2019.1）
     */
    private void initIcons() {
        // 新增图标
        this.add.setIcon(AllIcons.General.Add);
        // 删除图标
        this.del.setIcon(AllIcons.General.Remove);
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
     * @param tableModel 表格数据模型对象
     * @return Object[][]
     */
    private Object[][] getTableData(DefaultTableModel tableModel) {
        Vector vectors = tableModel.getDataVector();
        Object[][] objects = new Object[vectors.size()][];
        for (int i = 0; i < vectors.size(); i++) {
            Vector vector = (Vector) vectors.get(i);
            // 数据列类型
            String columnType = vector.get(0).toString();
            // 数据列对应的java类型
            String javaType = vector.get(1).toString();
            Object[] object = new Object[]{columnType, javaType};
            objects[i] = object;
        }
        return objects;
    }

    /**
     * 打开类型映射器
     */
    public void typeMapper() {
        DialogUtil dialogUtil = new DialogUtil();
        String title = Message.TYPE_MAPPER_OPEN.getTitle().concat(this.groupName);
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
        // 判断数据类型映射器中的配置信息是否存在修改，存在修改，启用apply按钮功能
        Object[][] objects = getTableData(getTableModel());
        return !this.typeMapper.equals(objects);
    }

    @Override
    public void apply() {
        // 当前模板组，数据类型映射，最新配置数据持久化
        Object[][] objects = getTableData(getTableModel());
        this.templateJUI.getTemplateUtil().getTemplate(this.groupName).setTypeMapper(objects);
    }
}
