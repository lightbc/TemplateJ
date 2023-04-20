package com.lightbc.templatej.components;

import lombok.Getter;
import lombok.Setter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * 带占位符的文本输入框组件
 */
public class TextField extends JTextField {
    @Getter
    @Setter
    private String placeholder;

    public TextField() {
    }

    public TextField(final Document pDoc, final String pText, final int pColumns) {
        super(pDoc, pText, pColumns);
    }

    public TextField(final int pColumns) {
        super(pColumns);
    }

    public TextField(final String pText) {
        super(pText);
    }

    public TextField(final String pText, final int pColumns) {
        super(pText, pColumns);
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, (int) (getHeight() * .6));
    }

}