package com.lightbc.templatej.test;

import com.lightbc.templatej.utils.CustomIconUtil;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Description:
 * Package:com.lightbc.templatej.test
 *
 * @author lightbc
 * @version 1.0
 */
public class Gif {
    public static void main(String[] args)
    {
        JFrame dialog = new JFrame("JEditorPane test");
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = dialog.getContentPane();

        c.setLayout(new BorderLayout());
        JButton button=new JButton();
        button.setIcon(new CustomIconUtil().getIcon(""));
        /*JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setText("<html><body><img src=\"https://gd-hbimg.huaban.com/e0a25a7cab0d7c2431978726971d61720732728a315ae-57EskW_fw1200\"></body></html>");*/
        c.add(new JScrollPane(button), BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }
}
