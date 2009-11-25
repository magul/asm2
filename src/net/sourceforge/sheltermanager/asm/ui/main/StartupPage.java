package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.ui.ui.*;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.*;

import java.util.*;


/**
 * Startup form that shows ASM news and the current diary
 * for today.
 */
public class StartupPage extends ASMForm {

    UI.HTMLBrowser diarynotes = null;
    UI.HTMLBrowser asmnews = null;
    UI.Label splash = null;

    public StartupPage() {
       init(Global.i18n("uimain", "Welcome"),
            IconManager.getIcon(IconManager.SCREEN_STARTUPPAGE), "uimain");

        new Thread() {
            public void run() {
                try {
                    DiaryNotesToday d = new DiaryNotesToday(false);
                    diarynotes.setPage("file:///" + d.getFilename());
                    asmnews.setPage(System.getProperty("asm.news", "http://sheltermanager.sf.net/startpage.html"));
                }
                catch (Exception e) {
                    Global.logException(e, getClass());
                }
            }
        }.start();
    }

    public Vector getTabOrder() {
        Vector v = new Vector();
        v.add(diarynotes);
        return v;
    }

    public Object getDefaultFocusedComponent() {
        return diarynotes;
    }

    public void initComponents() {
        
        diarynotes = UI.getHTMLBrowser();
        asmnews = UI.getHTMLBrowser();
        asmnews.setPreferredSize(UI.getDimension(400, 400));
        splash = UI.getLabel(IconManager.getSplashScreen());

        setLayout(UI.getBorderLayout());

        UI.Panel p = UI.getPanel(UI.getBorderLayout());
        p.add(splash, UI.BorderLayout.NORTH);
        UI.addComponent(p, asmnews);
        //p.add(asmnews, UI.BorderLayout.CENTER);
        
        UI.addComponent(this, diarynotes);
        //add(diarynotes, UI.BorderLayout.CENTER);
        add(p, UI.BorderLayout.EAST);
    }

    public void loadData() {
    }

    public boolean saveData() {
        return true;
    }

    public void setSecurity() {
    }

    public String getAuditInfo() {
        return null;
    }

    /** Return true to cancel the close */
    public boolean formClosing() {
        return false;
    }

}
