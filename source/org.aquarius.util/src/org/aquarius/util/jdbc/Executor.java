/**
 *
 */
package org.aquarius.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Execute command with the give statement.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
@FunctionalInterface
public interface Executor<S extends Statement> {

	/**
	 *
	 * @param connection
	 * @param statement
	 * @throws SQLException
	 */
	public void execute(Connection connection, S statement) throws SQLException;

}
