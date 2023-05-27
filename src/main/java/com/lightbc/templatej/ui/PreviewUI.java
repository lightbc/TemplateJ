package com.lightbc.templatej.ui;

import com.intellij.codeInsight.actions.*;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.lightbc.templatej.components.TextField;
import com.lightbc.templatej.entity.Generate;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.renderer.CustomJTreeRenderer;
import com.lightbc.templatej.utils.DataBaseUtil;
import com.lightbc.templatej.utils.EditorUtil;
import com.lightbc.templatej.utils.GenerateJUtil;
import com.lightbc.templatej.utils.ProjectUtil;
import lombok.Data;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 效果预览功能UI
 */
@Data
public class PreviewUI {
    // UI主面板
    private JPanel mainPanel;
    private JSplitPane splitPane;
    // 数据库树形结构组件
    private JTree tree;
    private JPanel searchPanel;
    // 模板名称
    private String templateFileName;
    // 显示内容
    private String content = "";
    // 编辑工具类
    private EditorUtil editorUtil;
    // 模板组名称
    private String groupName;
    // 模板内容
    private String sourceCode;
    // 数据库工具类
    private DataBaseUtil dataBaseUtil;
    // 选择的节点
    private DefaultMutableTreeNode selectTreeNode;
    private boolean autoPreview;

    public PreviewUI(String groupName, String templateFileName, String sourceCode, DataBaseUtil dataBaseUtil, boolean autoPreview) {
        this.groupName = groupName;
        this.templateFileName = templateFileName;
        this.sourceCode = sourceCode;
        this.dataBaseUtil = dataBaseUtil;
        this.autoPreview = autoPreview;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (autoPreview) {
            show();
        } else {
            initTree();
            initSearch();
            treeListener();
            refreshEditor(templateFileName, content);
        }
    }

    /**
     * 初始化查询功能
     */
    private void initSearch() {
        TextField tableNameSearch = new TextField();
        tableNameSearch.setPlaceholder(ConfigInterface.TABLE_NAME_SEARCH_PLACEHOLDER);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(tableNameSearch, BorderLayout.CENTER);
        // 查询节点
        tableNameSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String kw = tableNameSearch.getText();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
                DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectNode != null && selectNode.getChildCount() > 0) {
                    treeNode = selectNode;
                }
                searchTreeNode(kw, treeNode);
            }
        });
    }

    /**
     * 查询节点树
     *
     * @param kw       查询关键词
     * @param treeNode 树结构根节点
     */
    private void searchTreeNode(String kw, DefaultMutableTreeNode treeNode) {
        if (treeNode != null && treeNode.getChildCount() >= 0) {
            Enumeration childrens = treeNode.children();
            while (childrens.hasMoreElements()) {
                DefaultMutableTreeNode children = (DefaultMutableTreeNode) childrens.nextElement();
                if (children.toString().equals(kw.trim())) {
                    tree.setSelectionPath(new TreePath(children));
                    DefaultMutableTreeNode d = (DefaultMutableTreeNode) children.getParent();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                    tree.expandRow(root.getIndex(d));
                    return;
                }
                // 迭代查询
                searchTreeNode(kw, children);
            }
        }
    }

    /**
     * 初始化选择树
     */
    private void initTree() {
        // 获取已连接数源名称
        List<String> dbNames = dataBaseUtil.getDataSourceNames();
        if (dbNames != null && dbNames.size() > 0) {
            // 一级数据库节点
            DefaultMutableTreeNode node0 = new DefaultMutableTreeNode("DataBase");
            for (int i = 0; i < dbNames.size(); i++) {
                // 二级数据服务提供者节点
                DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(dbNames.get(i));
                node0.add(node1);
                Map<String, List<String>> map = dataBaseUtil.getDbTableMap(i);
                for (String key : map.keySet()) {
                    // 三级连接数据库节点
                    DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(key);
                    node1.add(node2);
                    List<String> list = map.get(key);
                    for (String s : list) {
                        // 四级数据表节点
                        DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(s);
                        node2.add(node3);
                    }
                }
            }
            DefaultTreeModel treeModel = new DefaultTreeModel(node0);
            tree.setModel(treeModel);
            CustomJTreeRenderer renderer = new CustomJTreeRenderer();
            // 自定义树形结构节点渲染
            renderer.setProvider(dataBaseUtil.getProvider());
            // 默认展开行设置
            tree.expandRow(1);
            tree.setCellRenderer(renderer);

        }
    }

    /**
     * 树形结构事件监听
     */
    private void treeListener() {
        tree.addTreeSelectionListener(e -> editorShow());
    }

    /**
     * 预览自定义模板效果
     */
    private void show() {
        GenerateJUtil generateJUtil = new GenerateJUtil();
        Map<String, Object> dataModel = generateJUtil.getCommonDataModel(null, ConfigInterface.DEFAULT_PACKAGE_NAME, null, new Generate(), null);
        String curContent = generateJUtil.generate(templateFileName, sourceCode, dataModel);
        refreshEditor(templateFileName, curContent);
    }

    /**
     * 编辑器内容显示
     */
    private void editorShow() {
        // 当前选择节点
        selectTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectTreeNode == null) {
            return;
        }
        String selectTableName = selectTreeNode.toString();
        // 根据当前选择的数据表名，获取对应的数源信息
        Map<String, DbDataSource> dataSourceMap = dataBaseUtil.getDataSourceMap();
        // 获取默认格式的树形结构的顶级节点名称
        String name = null;
        String schema = null;
        if (selectTreeNode.getChildCount() == 0) {
            name = selectTreeNode.getParent().getParent().toString();
            // 数据库名称
            schema = selectTreeNode.getParent().toString();
        }
        // 当前生成的预览内容
        String curContent = "";
        try {
            // 判断当前选择节点的指定父级节点是否为数据库节点
            if (name != null && schema != null && !"".equals(name.trim()) && !"DataBase".equals(name)) {
                // 当前选择的数据表
                DbTable dbTable = dataBaseUtil.getTable(dataSourceMap.get(name), schema, selectTableName);
                GenerateJUtil generateJUtil = new GenerateJUtil();
                // 获取数据模型
                Map<String, Object> dataModel = generateJUtil.getPreviewDataModel(groupName, dbTable);
                curContent = generateJUtil.generate(templateFileName, sourceCode, dataModel);
            }
        } catch (InvocationTargetException e) {
            curContent += "InvocationTargetException[消息内容：" + e.getMessage() + "\n诱发原因：" + e.getCause() + "]\n";
        } catch (Exception e) {
            curContent += "IllegalAccessException[消息内容：" + e.getMessage() + "\n诱发原因：" + e.getCause() + "]\n";
        } finally {
            refreshEditor(templateFileName, curContent);
        }
    }

    /**
     * 刷新编辑器的显示内容
     *
     * @param fileName 文件名
     * @param content  显示内容
     */
    private void refreshEditor(String fileName, String content) {
        EditorUtil editorUtil = new EditorUtil(content);
        JComponent editorComponent = editorUtil.getEditor(fileName, false);
        format(editorUtil.getEditor());
        splitPane.setRightComponent(editorComponent);
        if (autoPreview) {
            splitPane.getLeftComponent().setVisible(false);
        } else {
            splitPane.setDividerLocation(200);
            splitPane.setDividerSize(1);
        }
        editorUtil.highLighter(fileName, ProjectUtil.getProject());
    }

    /**
     * 格式化显示预览内容
     *
     * @param editor 编辑器对象
     */
    private void format(Editor editor) {
        PsiFile file = null;
        if (editor != null) {
            file = PsiDocumentManager.getInstance(ProjectUtil.getProject()).getPsiFile(editor.getDocument());
            if (file == null) {
                return;
            }
        }
        LastRunReformatCodeOptionsProvider provider = new LastRunReformatCodeOptionsProvider(PropertiesComponent.getInstance());
        assert file != null;
        ReformatCodeRunOptions options = provider.getLastRunOptions(file);
        TextRangeType scope = TextRangeType.WHOLE_FILE;
        options.setProcessingScope(scope);
        FileInEditorProcessor processor = new FileInEditorProcessor(file, editor, options);
        processor.processCode();
    }

}
