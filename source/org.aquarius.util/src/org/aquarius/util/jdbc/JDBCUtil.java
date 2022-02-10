/**
 *
 */
package org.aquarius.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.aquarius.util.io.IOUtil;

/**
 * JDBC Function provider.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class JDBCUtil {

	/**
	 * A specified executor will be invoked to complement a transaction.<BR>
	 *
	 * @param connection
	 * @param executor
	 * @throws SQLException
	 */
	public static void execute(DataSource dataSource, Executor<Statement> executor) throws SQLException {

		Connection connection = dataSource.getConnection();
		execute(connection, executor);
	}

	/**
	 * A specified executor will be invoked to complement a transaction.<BR>
	 *
	 * @param connection
	 * @param executor
	 * @param sql
	 * @throws SQLException
	 */
	public static void execute(DataSource dataSource, Executor<PreparedStatement> executor, String sql) throws SQLException {

		Connection connection = dataSource.getConnection();
		execute(connection, executor, sql);
	}

	/**
	 * A specified executor will be invoked to complement a transaction.<BR>
	 *
	 * @param connection
	 * @param executor
	 * @throws SQLException
	 */
	public static void execute(Connection connection, Executor<Statement> executor) throws SQLException {

		Statement statement = null;
		try {
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			executor.execute(connection, statement);

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			IOUtil.closeQuietly(statement);
			IOUtil.closeQuietly(connection);
		}
	}

	/**
	 * A specified executor will be invoked to complement a transaction.<BR>
	 *
	 * @param connection
	 * @param executor
	 * @param preparedSql
	 * @throws SQLException
	 */
	public static void execute(Connection connection, Executor<PreparedStatement> executor, String preparedSql) throws SQLException {
		PreparedStatement statement = null;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(preparedSql);
			executor.execute(connection, statement);

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			IOUtil.closeQuietly(statement);
			IOUtil.closeQuietly(connection);
		}
	}

}
