package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.awt.Font;


public class TitleLabel extends UI.Label {

    public TitleLabel(String title) {
        super(title);
        setFont(getFont().deriveFont(Font.BOLD, 16F));
    }

    public TitleLabel(String title, boolean drawLine) {
        super(title);
        setFont(getFont().deriveFont(Font.BOLD, 16F));
    }
}
