package io.github.yfblock.yfSql.Sql;

import io.github.yfblock.yfSql.Runner.SqlRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DataTableWrapper can store
 *
 * @param <T> the database object to be use
 */
public class DataTableWrapper<T> {
    private final SqlRunner sqlRunner;
    private final OrmHandler<T> ormHandler;


    /**
     * * construct the wrapper
     *
     * @param targetClass the target will be set
     * @param sqlRunner   the runner will be used to query sql
     */
    public DataTableWrapper(Class<T> targetClass, SqlRunner sqlRunner) {
        this.ormHandler = new OrmHandler<>(targetClass);
        this.sqlRunner  = sqlRunner;
    }

    /**
     * * construct the wrapper
     * @param sqlRunner sqlRunner
     */
    public DataTableWrapper(SqlRunner sqlRunner) {
        this.ormHandler = null;
        this.sqlRunner  = sqlRunner;
    }

    /**
     * execute sql query
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return results
     */
    public static<Y> ArrayList<Y> executeQuery(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        OrmHandler<Y> ormHandler = new OrmHandler<>(targetClass);
        System.out.println("executeQuery: " + sqlString);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        ArrayList<Y> targets = new ArrayList<>();
        while (resultSet.next()) {
            ormHandler.newTarget();
            for (String relationalKey : ormHandler.params.values()) {
                ormHandler.setRelationalKeyValue(relationalKey, resultSet.getObject(relationalKey));
            }
            targets.add(ormHandler.getTarget());
        }
        return targets;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be query
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return result
     */
    public static<Y> Y executeQueryFind(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        OrmHandler<Y> ormHandler = new OrmHandler<>(targetClass);
        System.out.println("executeQuery: " + sqlString);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        if (resultSet.next()) {
            ormHandler.newTarget();
            for (String relationalKey : ormHandler.params.values()) {
                ormHandler.setRelationalKeyValue(relationalKey, resultSet.getObject(relationalKey));
            }
            return ormHandler.getTarget();
        }
        return null;
    }

    /**
     * execute sql query once
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return result
     */
    public static<Y> Y executeQueryOneFieldFind(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        System.out.println("executeQuery: " + sqlString);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        // Return if there is data
        if (resultSet.next()) {
            return (Y) resultSet.getObject(1);
        }
        return null;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return result
     */
    public static<Y> ArrayList<Y> executeQueryOneField(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        System.out.println("executeQuery: " + sqlString);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        ArrayList<Y> targets = new ArrayList<>();
        while (resultSet.next()) {
            targets.add(resultSet.getObject(1, targetClass));
        }
        return targets;
    }


    /**
     * execute sql, if it is insert, then will return the primary key of the object has been inserted
     * @param sqlString the sql will be executed
     * @param sqlRunner sqlRunner
     * @return execute result or the key of the insert
     */
    public static int execute(String sqlString, SqlRunner sqlRunner) {
        try {
            System.out.println("execute: " + sqlString);
            ResultSet resultSet = sqlRunner.executeQuery(sqlString);
            if (resultSet != null && resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }

}
