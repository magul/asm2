package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.awt.Font;

import javax.swing.JLabel;


public class TitleLabel extends JLabel {
    Font font = new Font("Dialog", Font.BOLD, 16);

    public TitleLabel(String title) {
        super(title);
        setFont(font);
    }

    public TitleLabel(String title, boolean drawLine) {
        super(title);
        setFont(font);
    }
}
