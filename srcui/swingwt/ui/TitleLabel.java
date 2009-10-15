package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import swingwt.awt.Canvas;
import swingwt.awt.Dimension;
import swingwt.awt.Font;
import swingwt.awt.Graphics;

import swingwtx.swing.SwingWTUtils;


/**
 * Simple component that draws some text in the font of
 * your choice with a line either side.
 */
public class TitleLabel extends Canvas {
    Font font = new Font("Dialog", Font.BOLD | Font.ITALIC, 12);
    String title = "";
    private int width = 0;
    private int height = 0;
    private boolean drawLine = true;

    public TitleLabel(String title) {
        setText(title);
    }

    public TitleLabel(String title, boolean drawLine) {
        setText(title);
        this.drawLine = drawLine;
    }

    public void setText(String s) {
        title = s;
        width = Global.mainForm.getSize().width - 50;
        height = UI.getTextBoxHeight();
        setPreferredSize(new Dimension(width, height));
        repaint();
    }

    public String getText() {
        return title;
    }

    public void paint(Graphics g) {
        if (drawLine) {
            g.drawLine(2, 13, width - 3, 13);
        }

        g.setFont(font);
        g.drawString(title, 8, 3);
    }
}
