package net.sourceforge.sheltermanager.cursorengine.http;

import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public class HttpResultSet implements ResultSet {
    private final static String NULL_VALUE = "\\null";
    @SuppressWarnings("unused")
    private Connection c;
    @SuppressWarnings("unused")
    private String url;
    private ArrayList<String> lines;
    private ArrayList<String> colnames = new ArrayList<String>();
    private ArrayList<Integer> coltypes = new ArrayList<Integer>();
    private ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
    private int pos = -1;
    private final SimpleDateFormat df = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private boolean lastNull = false;

    public HttpResultSet(Connection c, String url, ArrayList<String> lines) {
        this.c = c;
        this.url = url;
        this.lines = lines;
        unpack();
    }

    /** Parses the response */
    private void unpack() {
        /*
        if (true) {
                System.out.println("GOT RESPONSE:");
                for (String s : lines) {
                        System.out.println(s);
                }
        }
        */
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Column descriptions?
            if (line.startsWith("COL")) {
                String[] cols = Utils.split(line.substring(3), "\\col");

                for (int z = 0; z < cols.length; z++) {
                    String[] col = Utils.split(cols[z], "\\type");
                    colnames.add(col[0]);

                    if (col[1].equals("integer") || col[1].equals("bigint")) {
                        coltypes.add(Types.INTEGER);
                    } else if (col[1].equals("varchar")) {
                        coltypes.add(Types.VARCHAR);
                    } else if (col[1].equals("timestamp")) {
                        coltypes.add(Types.TIMESTAMP);
                    } else if (col[1].startsWith("float")) {
                        coltypes.add(Types.FLOAT);
                    } else {
                        coltypes.add(Types.VARCHAR);
                    }
                }
            }

            // Row data
            if (line.startsWith("ROW")) {
                ArrayList<String> r = new ArrayList<String>();
                String[] data = Utils.split(line.substring(3), "\\fld");

                for (int z = 0; z < data.length; z++) {
                    r.add(data[z]);
                }

                rows.add(r);
            }
        }
    }

    @Override
    public boolean absolute(int arg0) throws SQLException {
        if (arg0 <= rows.size()) {
            return false;
        }

        pos = arg0 - 1;

        return true;
    }

    @Override
    public void afterLast() throws SQLException {
        pos = rows.size();
    }

    @Override
    public void beforeFirst() throws SQLException {
        pos = -1;
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void close() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public int findColumn(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean first() throws SQLException {
        pos = 0;

        return rows.size() > 0;
    }

    @Override
    public Array getArray(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Array getArray(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAsciiStream(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAsciiStream(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int arg0, int arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String arg0, int arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getBinaryStream(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getBinaryStream(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob getBlob(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob getBlob(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBoolean(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getBoolean(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte getByte(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte getByte(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] getBytes(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBytes(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getCharacterStream(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getCharacterStream(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clob getClob(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clob getClob(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getConcurrency() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCursorName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return null;
            }

            lastNull = false;

            return new java.sql.Date(df.parse(raw).getTime());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Date getDate(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(int arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDouble(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return 0;
            }

            lastNull = false;

            return Double.parseDouble(raw);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public double getDouble(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFetchSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getFloat(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return 0;
            }

            lastNull = false;

            return Float.parseFloat(raw);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public float getFloat(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return 0;
            }

            lastNull = false;

            return Integer.parseInt(raw);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int getInt(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLong(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return 0;
            }

            lastNull = false;

            return Long.parseLong(raw);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public long getLong(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new HttpResultSetMetaData(colnames, coltypes);
    }

    @Override
    public Reader getNCharacterStream(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getNCharacterStream(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob getNClob(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob getNClob(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNString(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNString(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(int arg0, Map<String, Class<?>> arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String arg0, Map<String, Class<?>> arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ref getRef(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ref getRef(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRow() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RowId getRowId(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RowId getRowId(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML getSQLXML(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML getSQLXML(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShort(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short getShort(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Statement getStatement() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getString(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return null;
            }

            lastNull = false;

            raw = Utils.replace(raw, "\\cr", "\r");
            raw = Utils.replace(raw, "\\lf", "\n");

            return raw;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getString(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(int arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(String arg0, Calendar arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(int arg0) throws SQLException {
        try {
            String raw = rows.get(pos).get(arg0 - 1);

            if (raw.equals(NULL_VALUE)) {
                lastNull = true;

                return null;
            }

            lastNull = false;

            return new java.sql.Timestamp(df.parse(raw).getTime());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Timestamp getTimestamp(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(int arg0, Calendar arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(String arg0, Calendar arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getType() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public URL getURL(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getURL(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insertRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isClosed() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean next() throws SQLException {
        pos++;

        if (pos == rows.size()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean previous() throws SQLException {
        pos--;

        return pos >= 0;
    }

    @Override
    public void refreshRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean relative(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setFetchDirection(int arg0) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setFetchSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateArray(int arg0, Array arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateArray(String arg0, Array arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBigDecimal(int arg0, BigDecimal arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBigDecimal(String arg0, BigDecimal arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(int arg0, Blob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(String arg0, Blob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(int arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(String arg0, InputStream arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBlob(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBoolean(int arg0, boolean arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBoolean(String arg0, boolean arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateByte(int arg0, byte arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateByte(String arg0, byte arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBytes(int arg0, byte[] arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateBytes(String arg0, byte[] arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(int arg0, Clob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(String arg0, Clob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(int arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(int arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateClob(String arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateDate(int arg0, Date arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateDate(String arg0, Date arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateDouble(int arg0, double arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateDouble(String arg0, double arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateFloat(int arg0, float arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateFloat(String arg0, float arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateInt(int arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateInt(String arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateLong(int arg0, long arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateLong(String arg0, long arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNCharacterStream(int arg0, Reader arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNCharacterStream(String arg0, Reader arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(int arg0, NClob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(String arg0, NClob arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(int arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(String arg0, Reader arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(int arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNClob(String arg0, Reader arg1, long arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNString(int arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNString(String arg0, String arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNull(int arg0) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateNull(String arg0) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateObject(int arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateObject(String arg0, Object arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateObject(int arg0, Object arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateObject(String arg0, Object arg1, int arg2)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateRef(int arg0, Ref arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateRef(String arg0, Ref arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateRow() throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateRowId(int arg0, RowId arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateRowId(String arg0, RowId arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateSQLXML(String arg0, SQLXML arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateShort(int arg0, short arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateShort(String arg0, short arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateString(int arg0, String arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateString(String arg0, String arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateTime(int arg0, Time arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateTime(String arg0, Time arg1) throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateTimestamp(int arg0, Timestamp arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateTimestamp(String arg0, Timestamp arg1)
        throws SQLException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean wasNull() throws SQLException {
        return lastNull;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
}


class HttpResultSetMetaData implements ResultSetMetaData {
    private ArrayList<String> colnames;
    private ArrayList<Integer> coltypes;

    public HttpResultSetMetaData(ArrayList<String> colnames,
        ArrayList<Integer> coltypes) {
        this.colnames = colnames;
        this.coltypes = coltypes;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return colnames.size();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return colnames.get(column - 1);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return colnames.get(column - 1);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return coltypes.get(column - 1).intValue();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
}
