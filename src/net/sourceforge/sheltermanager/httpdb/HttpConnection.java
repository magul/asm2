package net.sourceforge.sheltermanager.httpdb;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class HttpConnection implements Connection {

	public String url = null;
	
	public HttpConnection(String url) {
		this.url = url;
	}
	
	@Override
	public void clearWarnings() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void commit() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Blob createBlob() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Clob createClob() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Statement createStatement() throws SQLException {
		return new HttpStatement(this);
	}

	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Statement createStatement(int arg0, int arg1, int arg2) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public String getCatalog() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public String getClientInfo(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
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
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean isValid(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public String nativeSQL(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public CallableStatement prepareCall(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void rollback() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void rollback(Savepoint arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setAutoCommit(boolean arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setCatalog(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setHoldability(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setReadOnly(boolean arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setTransactionIsolation(int arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new IllegalArgumentException("Not supported");
	}

}
