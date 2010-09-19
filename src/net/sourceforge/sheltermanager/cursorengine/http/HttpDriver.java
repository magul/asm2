package net.sourceforge.sheltermanager.cursorengine.http;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import java.util.Properties;


public class HttpDriver implements Driver {
    static {
        try {
            // Register the Driver with DriverManager
            HttpDriver inst = new HttpDriver();
            DriverManager.registerDriver(inst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptsURL(String arg0) throws SQLException {
        return arg0.indexOf("http") != -1;
    }

    @Override
    public Connection connect(String arg0, Properties arg1)
        throws SQLException {
        if (arg0.indexOf("http") != -1) {
            return new HttpConnection(arg0);
        }

        throw new SQLException("Invalid URL: " + arg0);
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1)
        throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }
}
