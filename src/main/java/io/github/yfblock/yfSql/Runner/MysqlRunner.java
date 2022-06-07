package io.github.yfblock.yfSql.Runner;

import java.sql.*;

/**
 * mysql Runner
 * @author yufeng
 */
public class MysqlRunner implements SqlRunner {
    private Connection conn = null;

    /**
     * Constructor, init mysql connection
     * @param username database's username
     * @param password database's password
     * @param database database's name
     */
    public MysqlRunner(String username, String password, String database) {
        this("127.0.0.1", "3306", username, password, database);
    }

    /**
     * Constructor, init mysql connection
     * @param host     database's host
     * @param port     database's port
     * @param username database's username
     * @param password database's password
     * @param database database's name
     */
    public MysqlRunner(String host, String port, String username, String password, String database) {
        String connUrl = "jdbc:mysql://"+host+":"+port+"/"+database+
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true";
        String jdbc_driver = "com.mysql.cj.jdbc.Driver";
        try{
            Class.forName(jdbc_driver);
            this.conn = DriverManager.getConnection(connUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
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
