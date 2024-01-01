package com.lightbc.templatej.ui;

import com.intellij.codeInsight.actions.*;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.lightbc.templatej.action.EditorPopupMenuActionGroup;
import com.lightbc.templatej.components.TextField;
import com.lightbc.templatej.entity.Generate;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.interfaces.TemplateJInterface;
import com.lightbc.templatej.renderer.CustomJTreeRenderer;
import com.lightbc.templatej.utils.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
    // 默认查询数据库
    private DefaultMutableTreeNode schemaTreeNode;
    // 默认需要打开的数据库搜索节点
    private TreePath expandSchemaPath;
    // 插件自定义属性
    private PropertiesUtil propUtil;

    public PreviewUI(String groupName, String templateFileName, String sourceCode, DataBaseUtil dataBaseUtil, PropertiesUtil propUtil) {
        this.editorUtil = new EditorUtil();
        this.groupName = groupName;
        this.templateFileName = templateFileName;
        this.sourceCode = sourceCode;
        this.dataBaseUtil = dataBaseUtil;
        this.propUtil = propUtil;
        this.autoPreview = Boolean.parseBoolean(this.propUtil.getValue(TemplateJInterface.AUTO_PREVIEW));
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
                // 按下回车，进行查询
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (schemaTreeNode != null) {
                        // 查询完整关键字
                        String kw = tableNameSearch.getText();
                        searchTreeNode(kw, schemaTreeNode);
                    }
                }
            }
        });
    }

    /**
     * 查询节点树
     *
     * @param kw         查询关键词
     * @param schemaNode 数据库节点
     */
    private void searchTreeNode(String kw, DefaultMutableTreeNode schemaNode) {
        if (schemaNode != null) {
            // 遍历选择数据库节点下的所有数据表节点
            Enumeration childrens = schemaNode.children();
            while (childrens.hasMoreElements()) {
                DefaultMutableTreeNode children = (DefaultMutableTreeNode) childrens.nextElement();
                // 模糊匹配数据表节点，返回首次查询出的结果
                if (children.toString().contains(kw)) {
                    tree.setSelectionPath(new TreePath(children));
                    // 展开查询数据库节点子节点
                    if (expandSchemaPath != null) {
                        tree.expandPath(expandSchemaPath);
                    }
                    return;
                }
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
        tree.addTreeSelectionListener(this::editorShow);
    }

    /**
     * 预览自定义模板效果
     */
    private void show() {
        GenerateJUtil generateJUtil = new GenerateJUtil();
        Map<String, Object> dataModel = generateJUtil.getCommonDataModel(null, ConfigInterface.DEFAULT_PACKAGE_NAME, null, new Generate(), null);
        String curContent = generateJUtil.generate(templateFileName, sourceCode, dataModel, this.propUtil);
        refreshEditor(templateFileName, curContent);
    }

    /**
     * 编辑器内容显示
     *
     * @param event 树选择事件
     */
    private void editorShow(TreeSelectionEvent event) {
        // 当前选择节点
        selectTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectTreeNode == null) {
            return;
        }
        // 获取默认查询的数据库节点对象
        if (selectTreeNode.getLevel() == CustomJTreeRenderer.SECOND_LEVEL) {
            schemaTreeNode = selectTreeNode;
            expandSchemaPath = event.getPath();
        }
        // 根据当前选择的数据表名，获取对应的数源信息
        Map<String, DbDataSource> dataSourceMap = dataBaseUtil.getDataSourceMap();
        // 当前生成的预览内容
        String curContent = "";
        try {
            // 获取当前选择节点的级别
            int level = selectTreeNode.getLevel();
            // 判断选择的节点为数据库数据表节点，则进行预览内容生成
            if (level == CustomJTreeRenderer.THIRD_LEVEL) {
                // 数据源名称
                String dataSourceName = selectTreeNode.getParent().getParent().toString();
                // 数据库名称
                String schema = selectTreeNode.getParent().toString();
                // 选择的数据表名称
                String selectTableName = selectTreeNode.toString();
                // 当前选择的数据表
                DbTable dbTable = dataBaseUtil.getTable(dataSourceMap.get(dataSourceName), schema, selectTableName);
                GenerateJUtil generateJUtil = new GenerateJUtil();
                // 获取数据模型
                Map<String, Object> dataModel = generateJUtil.getPreviewDataModel(groupName, dbTable);
                curContent = generateJUtil.generate(templateFileName, sourceCode, dataModel, this.propUtil);
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
        JComponent editorComponent = editorUtil.getEditor(fileName, content, false);
        format(editorUtil.getEditor());
        splitPane.setRightComponent(editorComponent);
        if (autoPreview) {
            splitPane.getLeftComponent().setVisible(false);
        } else {
            splitPane.setDividerLocation(200);
            splitPane.setDividerSize(1);
        }
        editorUtil.highLighter(fileName, ProjectUtil.getProject());
        editorPopupMenuListener();
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

    /**
     * 编辑器右键属性菜单监听
     */
    private void editorPopupMenuListener() {
        this.editorUtil.getEditor().addEditorMouseListener(new EditorMouseListener() {
            @Override
            public void mouseReleased(@NotNull EditorMouseEvent event) {
                MouseEvent e = event.getMouseEvent();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (autoPreview) {
                        // 非数据接口，属性菜单显示导出功能
                        EditorPopupMenuActionGroup.setChildren(RightKeyUtil.getCustomTemplateExportActions(editorUtil.getEditor()));
                    } else {
                        // 数据表接口，属性菜单置为无效状态
                        EditorPopupMenuActionGroup.setChildren(null);
                    }
                }
            }
        });
    }

}
