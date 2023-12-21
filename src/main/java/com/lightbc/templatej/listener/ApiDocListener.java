package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:接口文档配置事件监听
 * Package:com.lightbc.templatej.listener
 *
 * @author lightbc
 * @version 1.0
 */
public class ApiDocListener {
    // 配置界面UI
    private TemplateJUI templateJUI;

    public ApiDocListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void init() {
        this.templateJUI.getApi().addActionListener(e -> apiDoc());
    }

    /**
     * 编辑接口文档内容
     */
    private void apiDoc() {
        String groupName = this.templateJUI.getCommonUI().getGroupName();
        if (StringUtils.isNotBlank(groupName)) {
            Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
            String content = template.getApiDoc();
            DialogUtil util = new DialogUtil();
            String title = Message.API_DCO.getTitle().concat(groupName);
            // API文档文件名（含默认拓展名）
            String fileName = groupName.concat(ConfigInterface.API_DOC_TYPE);
            // 显示API文档配置对话框
            int c = util.showApiDialog(title, fileName, content);
            //ok按钮按下后进行的后续操作
            if (c == 0) {
                String editedContent = util.getEditor().getDocument().getText();
                if (StringUtils.isNotBlank(editedContent)) {
                    // 保存编辑后的内容
                    template.setApiDoc(editedContent);
                } else {
                    util.showTipsDialog(null, Message.API_DOC_EMPTY.getMsg(), Message.API_DOC_EMPTY.getTitle());
                }
            }
        }
        // 刷新UI显示
        this.templateJUI.refresh();
    }

}
