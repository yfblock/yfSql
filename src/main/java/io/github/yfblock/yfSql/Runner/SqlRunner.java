package io.github.yfblock.yfSql.Runner;

import java.sql.ResultSet;

/**
 * SqlRunner interface, defined the rule of sqlRunner
 */
public interface SqlRunner {
    ResultSet executeQuery(String sqlString);       // execute sql
}
