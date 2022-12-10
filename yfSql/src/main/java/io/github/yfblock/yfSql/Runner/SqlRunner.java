package io.github.yfblock.yfSql.Runner;

import java.sql.*;

/**
 * SqlRunner interface, defined the rule of sqlRunner
 */
public class SqlRunner {
    protected Connection conn;

    public SqlRunner(String driver, String connUrl, String username, String password) {
        try{
            Class.forName(driver);
            this.conn = DriverManager.getConnection(connUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute sql
     * @param sqlString sql
     * @return the result
     * @throws SQLException throws sql exception
     */
    public ResultSet executeQuery(String sqlString) throws SQLException {
        PreparedStatement preparedStatement = this.conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.execute();
        if (sqlString.trim().indexOf("insert") == 0) return preparedStatement.getGeneratedKeys();
        else return preparedStatement.getResultSet();
    }
}
