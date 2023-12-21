package com.lightbc.templatej.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lightbc.templatej.entity.Generate;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJGenerateCommonUI;
import com.lightbc.templatej.ui.TemplateJGenerateUI;
import com.lightbc.templatej.utils.*;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 代码生成动作
 */
public class GenerateAction extends AnAction {
    // 选取的生成表
    @Setter
    private List<DbTable> tableList;
    // 生成器UI界面对象
    private TemplateJGenerateUI generateUI;

    GenerateAction(@Nullable String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DialogUtil dialogUtil = new DialogUtil();
        // 创建生成UI界面
        this.generateUI = new TemplateJGenerateUI();
        // 替换默认对话框确认操作按钮
        dialogUtil.setOkBtn(this.generateUI.getOkBtn());
        dialogUtil.setCancelBtn(this.generateUI.getCancelBtn());
        // 对话框事件监听
        dialogActionListener(dialogUtil);
        // 打开生成器UI界面对话框
        dialogUtil.showCustomDialog(this.getTemplateText(), this.generateUI.getMainPanel(), null, new Dimension(600, 400), false);
    }

    /**
     * 获取选择的生成模板项
     *
     * @param checkBoxParent 选取按钮的父级容器
     * @return List<String> 选择的生成模板项数组
     */
    private List<String> getCheckItems(JComponent checkBoxParent) {
        // 获取容器中的所有组件
        Component[] components = checkBoxParent.getComponents();
        List<String> list = new ArrayList<>();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                // 将容器组件转换成复选框组件对象
                JCheckBox box = (JCheckBox) component;
                // 获取被选中的复选框
                if (box.isSelected()) {
                    list.add(box.getText());
                }
            }
        }
        return list;
    }

    /**
     * 模板生成
     *
     * @param groupName     模板组名称
     * @param savePath      保存目录
     * @param checkFileList 选中的模板文件
     * @param commonUI      生成器通用UI对象
     */
    private void generate(String groupName, String savePath, List<String> checkFileList, TemplateJGenerateCommonUI commonUI) {
        // 选择生成的表对象若为空 ，退出执行（基本不会存在为空情况）
        if (this.tableList == null) {
            return;
        }
        // 未选中生成模板，退出执行
        if (checkFileList == null) {
            return;
        }
        String rootPath = ProjectUtil.getModulePath(this.generateUI.getSelectModule());
        // 判断是否禁用报错提示
        boolean closeTips = commonUI.getGenerateTips().isSelected();
        GenerateJUtil generateJUtil = new GenerateJUtil();
        generateJUtil.setCloseTips(closeTips);
        // 单表/多表选择，循环生成
        for (DbTable table : this.tableList) {
            // 使用选中的模板文件生成对应表的接口文件
            for (String fileName : checkFileList) {
                doGenerate(commonUI, generateJUtil, groupName, fileName, rootPath, savePath, table, closeTips);
            }
            // 生成API接口文档
            if (commonUI.getApiDoc().isSelected()) {
                commonUI.generateApiDoc(table, rootPath, generateJUtil);
            }
        }
    }

    /**
     * 执行模板生成操作
     *
     * @param commonUI      生成器通用UI对象
     * @param generateJUtil 代码生成处理工具类对象
     * @param groupName     模板组名称
     * @param fileName      模板文件文件名称
     * @param rootPath      项目模块的根路径
     * @param savePath      保存路径
     * @param table         数据表对象
     * @param closeTips     是否关闭提示消息，true-关闭，false-不关闭
     */
    private synchronized void doGenerate(TemplateJGenerateCommonUI commonUI, GenerateJUtil generateJUtil, String groupName, String fileName, String rootPath, String savePath, DbTable table, boolean closeTips) {
        try {
            String packageName = commonUI.getPackagePath().getText();
            // 模板数据模型对象
            Map<String, Object> dataModel = generateJUtil.getDataModel(groupName, table, fileName, rootPath, packageName, commonUI.getGeneratePath());
            // 自定义的保存文件名
            String saveFileName = ((Generate) dataModel.get(ConfigInterface.GENERATE_KEY_NAME)).getFileName();
            // 自定义保存路径部分
            String customPath = ((Generate) dataModel.get(ConfigInterface.GENERATE_KEY_NAME)).getSavePath();
            // 拼接自定义路径部分，获取完整路径
            savePath = generateJUtil.getCompleteSavePath(savePath, customPath);
            if ("".equals(savePath.trim())) {
                savePath = commonUI.getGenerateRoot();
            }
            // 生成模板
            generateJUtil.generate(groupName, fileName, saveFileName, savePath, dataModel);
        } catch (Exception e) {
            if (!closeTips) {
                DialogUtil dialog = new DialogUtil();
                dialog.showTipsDialog(null, String.format(Message.GENERATE_ERROR.getMsg(), fileName, e.getCause()), Message.GENERATE_ERROR.getTitle());
            }
        }
    }


    /**
     * 生成器对话框，确认生成事件监听
     *
     * @param dialog 生成器对话框
     */
    private void dialogActionListener(DialogUtil dialog) {
        JButton ok = dialog.getOkBtn();
        ok.addActionListener(e -> {
            // 兼容项目为多模块情况
            Map<String, TemplateJGenerateCommonUI> commonUIMaps = this.generateUI.getCommonUIMaps();
            if (commonUIMaps != null && commonUIMaps.size() > 0) {
                boolean noTemplate = true;
                for (String key : commonUIMaps.keySet()) {
                    TemplateJGenerateCommonUI ui = commonUIMaps.get(key);
                    // 获取选中的模板
                    List<String> checkList = getCheckItems(ui.getCheckboxContainerPanel());
                    // 模板组名称
                    String groupName = Objects.requireNonNull(ui.getGroupBox().getSelectedItem()).toString();
                    // 验证是否有模板被选择
                    if (!CommonUtil.validate(checkList)) {
                        continue;
                    }
                    generate(groupName, ui.getGeneratePath(), checkList, ui);
                    dialog.dispose();
                    noTemplate = false;
                }
                // 多模块都无选择模板时提示
                if (noTemplate) {
                    dialog.showTipsDialog(this.generateUI.getMainPanel(), Message.NO_CHOICE_GENERATE_TEMPLATE.getMsg(), Message.NO_CHOICE_GENERATE_TEMPLATE.getTitle());
                }
            } else {
                dialog.showTipsDialog(this.generateUI.getMainPanel(), Message.NO_CONFIG_MODULE.getMsg(), Message.NO_CONFIG_MODULE.getTitle());
            }
        });
    }

}
