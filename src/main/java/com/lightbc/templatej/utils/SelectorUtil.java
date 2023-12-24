package com.lightbc.templatej.utils;

import com.lightbc.templatej.DefaultTemplateParams;
import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:下拉选择器处理工具
 * Package:com.lightbc.templatej.utils
 *
 * @author lightbc
 * @version 1.0
 */
public class SelectorUtil {

    /**
     * 加载模板组选择器
     *
     * @param box    下拉组件
     * @param items  下拉选项
     * @param select 默认选择项
     */
    public static void loadGroupSelector(JComboBox box, List<String> items, String select) {
        if (items == null) {
            loadDefaultGroupSelector(box);
        } else {
            loadSelector(box, items, select);
        }
    }

    /**
     * 加载模板文件选择器
     *
     * @param box    下拉组件
     * @param items  下拉选项
     * @param select 默认选择项
     */
    public static void loadGroupFileSelector(JComboBox box, List<String> items, String select) {
        if (items == null) {
            items = new ArrayList<>();
            items.add(ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
        }
        if (!items.contains(ConfigInterface.DEFAULT_GROUP_FILE_VALUE)) {
            items.add(0, ConfigInterface.DEFAULT_GROUP_FILE_VALUE);
        }
        loadSelector(box, items, select);
    }

    /**
     * 加载默认的模板组选择器
     *
     * @param box 下拉组件
     */
    private static void loadDefaultGroupSelector(JComboBox box) {
        TemplateUtil templateUtil = new TemplateUtil(DefaultTemplateParams.getDefaultTemplates());
        loadGroupFileSelector(box, templateUtil.getGroupNames(), ConfigInterface.GROUP_NAME_VALUE);
    }

    /**
     * 加载下拉选择器
     *
     * @param box    下拉组件
     * @param items  下拉选项
     * @param select 默认选择项
     */
    public static void loadSelector(JComboBox box, List<String> items, String select) {
        if (box != null && items != null) {
            box.removeAllItems();
            box.setModel(getComboBoxModel(items));
            if (StringUtils.isNotBlank(select)) {
                box.setSelectedItem(select);
            } else {
                box.setSelectedIndex(0);
            }
        }
    }

    /**
     * 获取下拉选择器的数据模型
     *
     * @param items 下拉选项
     * @return DefaultComboBoxModel
     */
    private static DefaultComboBoxModel getComboBoxModel(List<String> items) {
        DefaultComboBoxModel model = null;
        if (items != null) {
            model = new DefaultComboBoxModel();
            for (String item : items) {
                model.addElement(item);
            }
        }
        return model;
    }

}
