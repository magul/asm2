/**
 *
 *  SQLRecordset Disconnected Recordset functionality
 *  and client-side cursor engine.
 *
 *  Copyright (C) 2002  Robin Rawson-Tetley
 *
 *  www.rawsontetley.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *
 *  <robin@rawsontetley.org>
 *
  */
package net.sourceforge.sheltermanager.cursorengine;

import net.sourceforge.sheltermanager.asm.bo.*;

import java.util.Iterator;


/**
 * A normal business object superclass. All business objects
 * which do not require stampable user info should extend this class.
 */
public abstract class NormalBO<T> implements Iterator<T>, Iterable<T> {
    /** The name of the table this object represents */
    protected String tableName = "";

    /** The local recordset */
    protected SQLRecordset rs = null;

    /** The SQL used to open the current set */
    protected String sql = "";

    /** Iterator looking at the first item? */
    private boolean firstItem = true;
    private int iteratorIndex = 0;

    /** Wraps recordset functionality and encapsulates where clause.
     * @param whereClause A valid SQL WHERE clause
     */
    public void openRecordset(String whereClause) {
        rs = new SQLRecordset();

        if (whereClause.equals("")) {
            try {
                sql = "Select * From " + tableName;
                rs.openRecordset(sql, tableName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                sql = "Select * From " + tableName + " Where " + whereClause;
                rs.openRecordset(sql, tableName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Returns the underlying recordset */
    public SQLRecordset getRecordset() {
        return rs;
    }

    /** Wraps recordset functionality and generates a new ID */
    public void addNew() throws CursorEngineException {
        rs.addNew();
        // Generate a primary key for the new record.
        rs.setField("ID", new Integer(DBConnection.getPrimaryKey(tableName)));
    }

    public void add() throws CursorEngineException {
        addNew();
    }

    /** Iterable::iterator */
    public Iterator<T> iterator() {
        try {
            iteratorIndex = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /** Iterator::hasNext */
    public boolean hasNext() {
        try {
            if (size() == 0) {
                return false;
            }

            return iteratorIndex < size();
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /** Iterator::next */
    public T next() {
        try {
            iteratorIndex++;
            rs.setAbsolutePosition(iteratorIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T) this;
    }

    /** Iterator::remove */
    public void remove() {
        try {
            delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Wraps recordset functionality */
    public void moveNext() throws CursorEngineException {
        rs.moveNext();
    }

    /** Wraps recordset functionality */
    public void movePrevious() throws CursorEngineException {
        rs.movePrevious();
    }

    /** Wraps recordset functionality */
    public void moveLast() throws CursorEngineException {
        rs.moveLast();
    }

    /** Wraps recordset functionality */
    public void moveFirst() throws CursorEngineException {
        rs.moveFirst();
    }

    /** Wraps recordset functionality */
    public long getRecordCount() {
        return rs.getRecordCount();
    }

    public void first() throws CursorEngineException {
        moveFirst();
    }

    public void last() throws CursorEngineException {
        moveLast();
    }

    public int size() {
        return rs.size();
    }

    /** Wraps recordset functionality */
    public boolean getBOF() {
        return rs.getBOF();
    }

    /** Wraps recordset functionality */
    public boolean getEOF() {
        return rs.getEOF();
    }

    /** Wraps recordset functionality */
    public void delete() throws CursorEngineException {
        rs.delete();
    }

    /** Inherited classes should override this method
     * with their validation routines.
     * @throws BOValidationException if there is a problem with the data.
     */
    public void validate() throws BOValidationException {
    }

    /**
     * Saves the recordset stored in the business object
     * after calling the validation routine first to
     * make sure it's ok.
     * If all is not ok, a <code>CursorEngineException</code> is
     * thrown to the caller containing the validation failure message.
     *
     * @throws CursorEngineException If validation fails.
     */
    public void save() throws CursorEngineException {
        try {
            // Perform validation before doing anything
            validate();

            // Make sure that somebody hasn't changed this
            // record before this save
            if (optimisticOk()) {
                rs.save(false, "");
            } else {
                throw new CursorEngineException(
                    "\nLOCKERR: Somebody changed this record while you were making your\nalterations.You must close and reopen the record,\nthen reapply your changes.");
            }
        } catch (Exception e) {
            throw new CursorEngineException(e.getMessage());
        }
    }

    /** If the table supports optimistic locking (has a RecordVersion
     *  field), then make sure that someone hasn't updated the record
     *  whilst we were looking at it
     */
    protected boolean optimisticOk() {
        final boolean debug = false;

        // If we are dealing with multiple records, then it's impossible
        // to identify which one we are talking about so do not do
        // optimistic locking. Make sure there aren't any nulls in
        // the records though.
        try {
            if (rs.getRecordCount() > 1) {
                if (debug) {
                    System.out.println(
                        "INFO: Cannot lock on multiple recordsets.");
                }

                if (!rs.getFieldExists("RecordVersion")) {
                    if (debug) {
                        System.out.println(
                            "INFO: No locking on this table anyway");
                    }

                    return true;
                }

                if (debug) {
                    System.out.println("INFO: Setting lock versions to 0");
                }

                int abs = rs.getAbsolutePosition();
                rs.moveFirst();

                while (!rs.getEOF()) {
                    if (rs.getField("RecordVersion") == null) {
                        rs.setField("RecordVersion", new Integer(0));
                    }

                    rs.moveNext();
                }

                rs.setAbsolutePosition(abs);

                return true;
            }
        } catch (Exception e) {
            if (debug) {
                System.out.println(
                    "ERR: Failed setting multiple set to version 0");
            }

            return true;
        }

        // If the optimistic lock option isn't on, don't test
        try {
            if (!Configuration.getBoolean("UseAnimalRecordLock")) {
                if (debug) {
                    System.out.println("INFO: Optimistic locking is disabled.");
                }

                rs.setField("RecordVersion", new Integer(0));

                return true;
            }
        } catch (Exception e) {
            if (debug) {
                System.out.println(
                    "ERR: Failed setting multiple set to version 0");
            }
        }

        Integer thisVersion = null;
        Integer recVersion = null;

        try {
            if (rs.getFieldExists("RecordVersion")) {
                thisVersion = (Integer) rs.getField("RecordVersion");

                // Set it to zero if we don't have a valid value (ie. New record)
                if (thisVersion == null) {
                    if (debug) {
                        System.out.println(
                            "INFO: No existing lock found - must be new record.");
                    }

                    thisVersion = new Integer(0);
                    rs.setField("RecordVersion", thisVersion);

                    return true;
                }
            } else {
                if (debug) {
                    System.out.println(
                        "INFO: Table has no lock field - can't do optimistic check.");
                }

                return true;
            }
        } catch (Exception e) {
            // Doesn't have a lock field so don't bother checking
            if (debug) {
                System.out.println("ERR: Couldn't read table lock version.");
            }

            return true;
        }

        try {
            // Get the current version from the database
            SQLRecordset cv = new SQLRecordset();
            cv.openRecordset(sql, tableName);

            // If there's no record, then it's ok - it's new
            if (cv.getEOF()) {
                if (debug) {
                    System.out.println(
                        "INFO: No existing record found - must be new.");
                }

                return true;
            }

            // Get the version
            recVersion = (Integer) cv.getField("RecordVersion");

            // Compare
            if (thisVersion.equals(recVersion)) {
                // It's ok - update the version and save
                int cl = thisVersion.intValue();
                cl++;
                rs.setField("RecordVersion", new Integer(cl));

                if (debug) {
                    System.out.println("INFO: Tags match - update ok.");
                }

                return true;
            } else {
                // Someone has changed this record
                if (debug) {
                    System.out.println("ERROR: Record has been changed.");
                }

                return false;
            }
        } catch (Exception e) {
            // Something went wrong getting the lock - ignore the check
            if (debug) {
                System.out.println(
                    "WARNING: Couldn't read lock from existing record.");
            }

            return true;
        }
    }

    public void free() {
        rs.free();
        rs = null;
        tableName = null;
        sql = null;
    }
}
