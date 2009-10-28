package net.sourceforge.sheltermanager.asm.ui.ui;

import net.sourceforge.sheltermanager.asm.globals.Global;

import java.awt.Font;

import javax.swing.*;
import javax.swing.border.*;


public class TitleLabel extends UI.Label {
    public TitleLabel(String title) {
        super(title);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setFont(getFont().deriveFont(Font.BOLD, 16F));
    }

    public TitleLabel(String title, boolean drawLine) {
        super(title);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setFont(getFont().deriveFont(Font.BOLD, 16F));
    }
}
