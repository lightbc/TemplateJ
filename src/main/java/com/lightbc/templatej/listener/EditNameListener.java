package com.lightbc.templatej.listener;

import com.lightbc.templatej.entity.Template;
import com.lightbc.templatej.enums.Message;
import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.TemplateJCommonUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;
import com.lightbc.templatej.utils.TemplateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 模板组/模板文件，重命名按钮监听
 */
public class EditNameListener {
    private TemplateJUI templateJUI;
    private TemplateJCommonUI commonUI;

    public EditNameListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        init();
    }

    private void loadCommonUI() {
        this.commonUI = new TemplateJCommonUI(this.templateJUI.getSettings(), this.templateJUI.getTemplateUtil());
    }

    private void init(){
        loadCommonUI();
        editName();
    }

    /**
     * 重命名功能
     */
    public void editName() {
        this.templateJUI.getEditName().addActionListener((e) -> {
            loadCommonUI();
            this.commonUI.disable();
            this.commonUI.showRename();
            rename();
        });
    }

    /**
     * 重命名
     */
    public void rename() {
        DialogUtil dialogUtil = new DialogUtil();
        // 重命名默认的模板组/模板文件错误提示
        if (isDefaultTemplate(this.commonUI.getGroupName(), this.commonUI.getGroupFileName())) {
            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_DEFAULT_TEMPLATE_FAIL.getMsg(), Message.RENAME_DEFAULT_TEMPLATE_FAIL.getTitle());
            return;
        }
        int c = dialogUtil.showConfirmDialog(this.templateJUI.getMainPanel(), this.commonUI.getMainPanel(), Message.RENAME_TITLE.getTitle());
        // 0：确认操作
        if (c == 0) {
            doRename();
        }
    }

    /**
     * 判断需要删除的模板是不是默认的模板
     *
     * @param groupName        模板组名称
     * @param templateFileName 模板文件名称
     * @return boolean 是-true，否-false
     */
    private boolean isDefaultTemplate(String groupName, String templateFileName) {
        // 是否是默认模板组
        if (ConfigInterface.GROUP_NAME_VALUE.equals(groupName.trim()) && !TemplateUtil.isTemplateFile(templateFileName)) {
            return true;
        }
        // 是否是默认模板组的默认模板文件
        if (ConfigInterface.GROUP_NAME_VALUE.equals(groupName.trim()) && TemplateUtil.isTemplateFile(templateFileName)) {
            String[] groupFile = ConfigInterface.GROUP_FILE_VALUE;
            for (String file : groupFile) {
                if (file.equals(templateFileName.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重命名操作
     */
    private void doRename() {
        DialogUtil dialogUtil = new DialogUtil();
        String rename = this.commonUI.getRename().getText();
        // 重命名为空提示
        if (StringUtils.isBlank(rename)) {
            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_IS_BLANK.getMsg(), Message.RENAME_IS_BLANK.getTitle());
            return;
        }
        // 原模板组名称
        String groupName = this.commonUI.getGroupName();
        // 模板文件名称
        String selectGroupFile = this.commonUI.getGroupFileName();
        // 选择的模板
        Template template = this.templateJUI.getTemplateUtil().getTemplate(groupName);
        boolean b = this.templateJUI.getSettings().getTemplates().contains(template);
        // 判断进行操作的模板对象是否存在
        if (b) {
            //重命名模板组/模板文件名称
            if (TemplateUtil.isTemplateFile(selectGroupFile)) {
                List<String> groupFiles = template.getGroupFiles();
                // 判断模板文件名称是否存在
                if (groupFiles.contains(rename)) {
                    dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_GROUP_FILE_EXIST.getMsg(), Message.RENAME_GROUP_FILE_EXIST.getTitle());
                    return;
                }
                // 更新模板文件信息
                this.templateJUI.getTemplateUtil().editGroupFileName(groupName, selectGroupFile, rename);
                // 设置默认选择模板文件
                this.templateJUI.getSettings().setSelectGroupFile(rename);
            } else {
                // 判断模板组名称是否存在
                List<Template> templates = this.templateJUI.getSettings().getTemplates();
                if (templates != null) {
                    for (Template tp : templates) {
                        if (tp.getGroupName().equals(rename)) {
                            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.RENAME_GROUP_EXIST.getMsg(), Message.RENAME_GROUP_EXIST.getTitle());
                            return;
                        }
                    }
                }
                // 更新模板组信息
                this.templateJUI.getTemplateUtil().editGroupName(groupName, rename);
                // 设置默认选择模板组
                this.templateJUI.getSettings().setSelectGroup(rename);
            }
            this.templateJUI.refresh();
        } else {
            dialogUtil.showTipsDialog(this.templateJUI.getMainPanel(), Message.OPERATE_TEMPLATE_NOT_EXIST.getMsg(), Message.OPERATE_TEMPLATE_NOT_EXIST.getTitle());
            this.templateJUI.refresh();
            return;
        }
    }

}
