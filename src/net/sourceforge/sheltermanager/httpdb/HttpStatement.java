package net.sourceforge.sheltermanager.httpdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class HttpStatement implements Statement {

	private HttpConnection c = null;
	private String url = null;
	
	public HttpStatement(HttpConnection c) {
		this.c = c;
		this.url = c.url;
	}
	
	@Override
	public void addBatch(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void cancel() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void clearBatch() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int[] executeBatch() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return c;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getMaxRows() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getResultSetType() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return false;
	}

	@Override
	public void setCursorName(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setMaxRows(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

}
