package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.NormalBO;


public class LogType extends NormalBO {
    public LogType() {
        tableName = "logtype";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getLogTypeName() throws CursorEngineException {
        return (String) rs.getField("LogTypeName");
    }

    public void setLogTypeName(String newValue) throws CursorEngineException {
        rs.setField("LogTypeName", newValue);
    }

    public String getLogTypeDescription() throws CursorEngineException {
        return (String) rs.getField("LogTypeDescription");
    }

    public void setLogTypeDescription(String newValue)
        throws CursorEngineException {
        rs.setField("LogTypeDescription", newValue);
    }

    public void validate() throws BOValidationException {
    }
}
