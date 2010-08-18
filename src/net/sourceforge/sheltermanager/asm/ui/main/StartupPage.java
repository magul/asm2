package net.sourceforge.sheltermanager.asm.ui.main;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.reports.*;
import net.sourceforge.sheltermanager.asm.ui.reportviewer.ReportViewer;
import net.sourceforge.sheltermanager.asm.ui.system.FileTypeManager;
import net.sourceforge.sheltermanager.asm.ui.ui.*;

import java.util.*;


/**
 * Startup form that shows ASM news and the current diary
 * for today.
 */
public class StartupPage extends ASMForm {
    //UI.HTMLBrowser diarynotes = null;
    ReportViewer diarynotes = null;
    UI.HorizontalSplitPane hs = null;
    UI.HTMLBrowser asmnews = null;
    UI.Label splash = null;

    public StartupPage() {
        init(Global.i18n("uimain", "Welcome"),
            IconManager.getIcon(IconManager.SCREEN_STARTUPPAGE), "uimain");

        // Run the diary report on a separate thread
        UI.cursorToWait();
        new Thread() {
                public void run() {
                    try {
                        final DiaryNotesToday d = new DiaryNotesToday(false);

                        // Use the dispatch thread to load the report content
                        UI.invokeLater(new Runnable() {
                            public void run() {
                                diarynotes.showReport(d.getFilename(), d.getTitle());
                                // 400px for image width, plus 4px*2 for panel margin,
                                // plus 8px for splitter width
                                hs.setDividerLocation(getWidth() - 416);
                                UI.cursorToPointer();
                            }
                        });

                        // Load the news after the report - still on a separate thread
                        // so there's no blocking if we can't get to the web
                        asmnews.setPage(System.getProperty("asm.news",
                            "http://sheltermanager.sf.net/startpage.html"));

                    } catch (Exception e) {
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
        
        diarynotes = new ReportViewer();
        
        UI.Panel pnews = UI.getPanel(UI.getBorderLayout());
        pnews.add(UI.getSplashLabel(), UI.BorderLayout.NORTH);
        asmnews = UI.getHTMLBrowser(new FunctionPointer(this, "hyperlinkClicked", new Class[] { String.class }));
        asmnews.setPreferredSize(UI.getDimension(400, 400));
        UI.addComponent(pnews, asmnews);

        setLayout(UI.getBorderLayout());
        hs = UI.getHorizontalSplitPane(diarynotes, pnews);
        add(hs);
    }

    public void hyperlinkClicked(String target) {
        FileTypeManager.shellExecute(target);
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
