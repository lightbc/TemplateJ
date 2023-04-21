package com.lightbc.templatej.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lightbc.templatej.entity.Generate;
import com.lightbc.templatej.enums.Message;
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
    // 生成的保存路径
    private String generatePath;
    // 生成器UI界面对象
    private TemplateJGenerateUI generateUI;

    GenerateAction(@Nullable String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DialogUtil dialogUtil = new DialogUtil();
        // 创建生成UI界面
        generateUI = new TemplateJGenerateUI();
        // 替换默认对话框确认操作按钮
        dialogUtil.setOkBtn(generateUI.getOkBtn());
        dialogUtil.setCancelBtn(generateUI.getCancelBtn());
        // 对话框事件监听
        dialogActionListener(dialogUtil);
        // 打开生成器UI界面对话框
        dialogUtil.showCustomDialog(this.getTemplateText(), generateUI.getMainPanel(), null, new Dimension(500, 340), false);
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
     * 执行生成操作
     *
     * @param groupName     模板组名称
     * @param savePath      保存目录
     * @param checkFileList 选中的模板文件
     */
    private void generate(String groupName, String savePath, List<String> checkFileList) {
        // 选择生成的表对象若为空 ，退出执行（基本不会存在为空情况）
        if (tableList == null) {
            return;
        }
        // 未选中生成模板，退出执行
        if (checkFileList == null) {
            return;
        }
        // 单表/多表选择，循环生成
        for (DbTable table : tableList) {
            // 使用选中的模板文件生成对应表的接口文件
            for (String fileName : checkFileList) {
                // 模板文件名
                GenerateJUtil generateJUtil = new GenerateJUtil();
                // 判断是否禁用报错提示
                boolean closeTips = generateUI.getGenerateTips().isSelected();
                try {
                    // 模板数据模型对象
                    Map<String, Object> dataModel = generateJUtil.getDataModel(groupName, table, fileName, generateUI, generatePath);
                    // 自定义的保存文件名
                    String saveFileName = ((Generate) dataModel.get("generate")).getFileName();
                    // 自定义保存路径部分
                    String customPath = ((Generate) dataModel.get("generate")).getSavePath();
                    // 拼接自定义路径部分，获取完整路径
                    savePath = generateJUtil.getCompleteSavePath(savePath, customPath);
                    if ("".equals(savePath)) {
                        savePath = generateUI.getGenerateRoot();
                    }
                    // 生成模板
                    generateJUtil.generate(groupName, fileName, saveFileName, savePath, dataModel, closeTips);
                } catch (Exception e) {
                    if (!closeTips) {
                        DialogUtil dialog = new DialogUtil();
                        dialog.showTipsDialog(null, String.format(Message.GENERATE_ERROR.getMsg(), fileName, e.getCause()), Message.GENERATE_ERROR.getTitle());
                    }
                }
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
            generatePath = generateUI.getGeneratePath();
            // 获取选中的模板
            List<String> checkList = getCheckItems(generateUI.getCheckboxContainerPanel());
            // 模板组名称
            String groupName = Objects.requireNonNull(generateUI.getGroupBox().getSelectedItem()).toString();
            // 验证是否有模板被选择
            if (!validate(checkList)) {
                dialog.showTipsDialog(null, Message.NO_CHOICE_GENERATE_TEMPLATE.getMsg(), Message.NO_CHOICE_GENERATE_TEMPLATE.getTitle());
                return;
            }
            generate(groupName, generatePath, checkList);
            dialog.dispose();
        });
    }

    /**
     * 对象是否为空验证
     *
     * @param o 验证对象
     * @return boolean 不为空：true，空：false
     */
    private boolean validate(Object o) {
        if (o == null) {
            return false;
        }
        // 数组类型验证
        if (o instanceof List) {
            List list = (List) o;
            if (list.size() > 0) {
                return true;
            }
        }
        // 字符串类型验证
        if (o instanceof String) {
            String s = String.valueOf(o);
            return !"".equals(s.trim());
        }
        return false;
    }

}
