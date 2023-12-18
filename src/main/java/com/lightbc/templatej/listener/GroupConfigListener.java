package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 全局配置项配置事件监听
 */
@Slf4j
public class GroupConfigListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public GroupConfigListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        this.templateJUI.getConfigBtn().addActionListener(e -> editConfig());
    }

    /**
     * 编辑全局配置文件配置信息
     */
    private void editConfig() {
        String groupName = this.templateJUI.getCommonUI().getGroupName();
        if (StringUtils.isNotBlank(groupName)) {
            String title = Message.GLOBAL_CONFIG.getTitle().concat(groupName);

            // 全局配置文件名（含默认拓展名）
            String fileName = groupName.concat(ConfigInterface.SETTING_TYPE);
            Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
            String content = template.getGlobalConfig();
            DialogUtil util = new DialogUtil();

            // 显示全局配置对话框
            int c = util.showConfigDialog(title, fileName, content);
            //ok按钮按下后进行的后续操作
            if (c == 0) {
                String editedContent = util.getEditor().getDocument().getText();
                if (StringUtils.isNotBlank(editedContent)) {
                    // 保存编辑后的内容
                    template.setGlobalConfig(editedContent);
                    // 刷新UI显示
                    this.templateJUI.refresh();
                } else {
                    util.showTipsDialog(null, Message.GLOBAL_CONFIG_EMPTY.getMsg(), Message.GLOBAL_CONFIG_EMPTY.getTitle());
                }
            }
        }
    }
}
