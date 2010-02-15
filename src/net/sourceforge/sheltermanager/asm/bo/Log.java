package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;


public class Log extends UserInfoBO {
    public static final int LINKTYPE_ANIMAL = 0;
    public static final int LINKTYPE_OWNER = 1;
    public static final int LINKTYPE_LOSTANIMAL = 2;
    public static final int LINKTYPE_FOUNDANIMAL = 3;
    public static final int LINKTYPE_WAITINGLIST = 4;
    public static final int LINKTYPE_MOVEMENT = 5;

    public Log() {
        tableName = "log";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public Log copy() throws CursorEngineException {
        Log l = new Log();
        l.openRecordset("ID = 0");
        l.addNew();
        l.setLogTypeID(getLogTypeID());
        l.setLinkID(getLinkID());
        l.setLinkType(getLinkType());
        l.setDate(getDate());
        l.setComments(getComments());

        return l;
    }

    public Integer getLogTypeID() throws CursorEngineException {
        return (Integer) rs.getField("LogTypeID");
    }

    public void setLogTypeID(Integer newValue) throws CursorEngineException {
        rs.setField("LogTypeID", newValue);
    }

    public String getLogTypeName() throws CursorEngineException {
    	return LookupCache.getLogTypeName(getLogTypeID());
    }
    
    public String getLogTypeDescription() throws CursorEngineException {
    	return LookupCache.getLogTypeDescription(getLogTypeID());
    }

    public Integer getLinkID() throws CursorEngineException {
        return (Integer) rs.getField("LinkID");
    }

    public void setLinkID(Integer newValue) throws CursorEngineException {
        rs.setField("LinkID", newValue);
    }

    public Integer getLinkType() throws CursorEngineException {
        return (Integer) rs.getField("LinkType");
    }

    public void setLinkType(Integer newValue) throws CursorEngineException {
        rs.setField("LinkType", newValue);
    }

    public Date getDate() throws CursorEngineException {
        return (Date) rs.getField("Date");
    }

    public void setDate(Date newValue) throws CursorEngineException {
        rs.setField("Date", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public Integer getRecordVersion() throws CursorEngineException {
        return (Integer) rs.getField("RecordVersion");
    }

    public void setRecordVersion(Integer newValue) throws CursorEngineException {
        rs.setField("RecordVersion", newValue);
    }

    public String getCreatedBy() throws CursorEngineException {
        return (String) rs.getField("CreatedBy");
    }

    public void setCreatedBy(String newValue) throws CursorEngineException {
        rs.setField("CreatedBy", newValue);
    }

    public Date getCreatedDate() throws CursorEngineException {
        return (Date) rs.getField("CreatedDate");
    }

    public void setCreatedDate(Date newValue) throws CursorEngineException {
        rs.setField("CreatedDate", newValue);
    }

    public String getLastChangedBy() throws CursorEngineException {
        return (String) rs.getField("LastChangedBy");
    }

    public void setLastChangedBy(String newValue) throws CursorEngineException {
        rs.setField("LastChangedBy", newValue);
    }

    public Date getLastChangedDate() throws CursorEngineException {
        return (Date) rs.getField("LastChangedDate");
    }

    public void setLastChangedDate(Date newValue) throws CursorEngineException {
        rs.setField("LastChangedDate", newValue);
    }

    public void validate() throws BOValidationException {
        try {
            if (getDate() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_a_date"));
            }

            if ((getLogTypeID() == null) || (getLogTypeID().intValue() == 0)) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_a_log_type"));
            }

            // if (getComments().equals(SQLRecordset.NULL_VALUE) ||
            // getComments().equals(""))
            // throw new BOValidationException(Global.i18n("bo",
            // "You_must_supply_the_log_entry"));
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
