package com.lightbc.templatej.listener;

import com.lightbc.templatej.interfaces.ConfigInterface;
import com.lightbc.templatej.ui.LoggerUI;
import com.lightbc.templatej.ui.TemplateJUI;
import com.lightbc.templatej.utils.DialogUtil;

import java.awt.*;

/**
 * Description:日志功能事件监听
 * Package:com.lightbc.templatej.listener
 *
 * @author lightbc
 * @version 1.0
 */
public class LogListener {
    private TemplateJUI templateJUI;

    public LogListener(TemplateJUI templateJUI) {
        this.templateJUI = templateJUI;
        this.templateJUI.getPluginLogs().addActionListener(e -> logAction());
    }

    private void logAction() {
        DialogUtil dialog = new DialogUtil();
        LoggerUI loggerUI = new LoggerUI();
        dialog.showNoButtonDialog(ConfigInterface.PLUGIN_LOGGING, null, loggerUI.getMainPanel(), new Dimension(1000, 600));
    }
}
