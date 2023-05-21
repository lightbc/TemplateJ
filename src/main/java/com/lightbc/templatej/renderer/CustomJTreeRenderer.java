package com.lightbc.templatej.renderer;

import com.intellij.icons.AllIcons;
import icons.DatabaseIcons;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * 自定义树形结构渲染器
 */
public class CustomJTreeRenderer extends DefaultTreeCellRenderer {
    // 数据服务提供者
    @Getter
    @Setter
    private String provider;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        String nodeName = treeNode.toString();
        return renderer(nodeName, treeNode.getLevel());
    }

    /**
     * 自定义渲染配置
     *
     * @param nodeName 节点名称
     * @param level    节点级别
     * @return JLabel 渲染显示组件
     */
    private JLabel renderer(String nodeName, int level) {
        JLabel label = new JLabel();
        if (nodeName != null && !"".equals(nodeName.trim()) && level > -1) {
            // 判断节点是否为数据库节点
            if ("DataBase".equals(nodeName) && level == 0) {
                label.setIcon(DatabaseIcons.Dbms);
            }
            // 判断节点是否为数据服务提供者节点
            if (level == 1) {
                label.setIcon(getProviderIcon());
            }
            // 判断节点是否为数据库节点
            if (level == 2) {
                label.setIcon(DatabaseIcons.Schema);
            }
            // 判断节点是否是数据库表节点
            if (level == 3) {
                label.setIcon(AllIcons.Nodes.DataTables);
            }
            label.setText(nodeName);
        }
        return label;
    }

    /**
     * 获取数据服务提供者
     *
     * @return 数据服务提供者系统图标-2019.1
     */
    private Icon getProviderIcon() {
        if (provider != null) {
            // MySQL
            if ("mysql".equals(provider.toLowerCase())) {
                return AllIcons.Providers.Mysql;
            }
            // Oracle
            if ("oracle".equals(provider.toLowerCase())) {
                return AllIcons.Providers.Oracle;
            }
            // Microsoft SQL Server
            if ("mssql".equals(provider.toLowerCase())) {
                return AllIcons.Providers.SqlServer;
            }
        }
        return AllIcons.Modules.SourceRoot;
    }
}
