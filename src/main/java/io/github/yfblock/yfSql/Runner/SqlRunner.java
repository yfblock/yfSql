package io.github.yfblock.yfSql.Runner;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SqlRunner interface, defined the rule of sqlRunner
 */
public interface SqlRunner {
    ResultSet executeQuery(String sqlString) throws SQLException;       // execute sql
}
