package net.sourceforge.sheltermanager.httpdb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

public class HttpDriver implements Driver {

	static {
		try {
			// Register with the DriverManager
			HttpDriver inst = new HttpDriver();
			DriverManager.registerDriver(inst);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		if (url.startsWith("http")) return true;
		return false;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		return new HttpConnection(url);
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

}
