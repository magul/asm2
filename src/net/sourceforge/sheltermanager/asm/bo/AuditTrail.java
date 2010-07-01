/*
 Animal Shelter Manager
 Copyright(c)2000-2010, R. Rawson-Tetley

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTIBILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston
 MA 02111-1307, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.utility.Utils;
import net.sourceforge.sheltermanager.cursorengine.DBConnection;

import java.util.Calendar;


/**
 * Class for handling creation of audit trail items
 */
public final class AuditTrail {
    private final static int CREATE = 0;
    private final static int CHANGE = 1;
    private final static int DELETE = 2;
    private final static int MOVE = 3;

    /** Purely static class */
    private AuditTrail() {
    }

    public static void create(String table, String id) {
        if (!enabled()) {
            return;
        }

        audit(table, Global.i18n("bo", "audit_create", id), CREATE);
    }

    public static void changed(String table, String id) {
        if (!enabled()) {
            return;
        }

        audit(table, Global.i18n("bo", "audit_changed", id), CHANGE);
    }

    public static void deleted(String table, String id) {
        if (!enabled()) {
            return;
        }

        audit(table, Global.i18n("bo", "audit_deleted", id), DELETE);
    }

    public static void moved(String table, String id, String from, String to) {
        if (!enabled()) {
            return;
        }

        audit(table, Global.i18n("bo", "audit_moved", id, from, to), MOVE);
    }

    public static void merged(String table, String from, String to) {
        if (!enabled()) {
            return;
        }

        audit(table, Global.i18n("bo", "audit_merged", from, to), DELETE);
    }

    public static void updated(boolean isNew, String table, String id) {
        if (isNew) {
            create(table, id);
        } else {
            changed(table, id);
        }
    }

    public static boolean enabled() {
        return Configuration.getBoolean("AdvancedAudit");
    }

    private static void audit(String table, String description, int action) {
        try {
            if (Global.showDebug) {
                Global.logDebug("AUDIT/" + action + " " + table + ": " +
                    description, "AuditTrail.audit");
            }

            String sql = "INSERT INTO audittrail VALUES (" + action + ", " +
                "'" + Utils.getSQLDate(Calendar.getInstance()) + "', " + "'" +
                Global.currentUserName + "', " + "'" + table + "', " + "'" +
                description.replace('\'', '`') + "')";
            DBConnection.executeAction(sql);
        } catch (Exception e) {
            Global.logException(e, AuditTrail.class);
        }
    }
}
