package com.lightbc.templatej.ui;

import lombok.Data;

import javax.swing.*;

/**
 * Description:日志UI界面
 * Package:com.lightbc.templatej.ui
 *
 * @author lightbc
 * @version 1.0
 */
@Data
public class LoggerUI {
    private JPanel mainPanel;
    private JComboBox timeRangeSelector;
    private JButton clearBtn;
    private JTextArea logShow;
}
