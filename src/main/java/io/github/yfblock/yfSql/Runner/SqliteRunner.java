package io.github.yfblock.yfSql.Runner;

import java.sql.*;

public class SqliteRunner implements SqlRunner{
    private Connection conn = null;

    /**
     * SqliteRunner Constructor, build a sqlite runner
     * @param databaseFilePath sqlitePath
     */
    public SqliteRunner(String databaseFilePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * execute sql, return result
     * @param sqlString the sql to be executed
     * @return ResultSet
     */
    @Override
    public ResultSet executeQuery(String sqlString) {
        try {
            PreparedStatement preparedStatement = this.conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.execute();
            if (sqlString.trim().indexOf("insert") == 0) return preparedStatement.getGeneratedKeys();
            else return preparedStatement.getResultSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
