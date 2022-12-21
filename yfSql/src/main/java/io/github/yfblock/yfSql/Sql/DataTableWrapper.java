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
     * @throws SQLException sql execute exception
     * @return results
     */
    public static<Y> ArrayList<Y> executeQuery(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        OrmHandler<Y> ormHandler = new OrmHandler<>(targetClass);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        ArrayList<Y> targets = new ArrayList<>();
        while (resultSet.next()) {
            targets.add(DataTableWrapper.updateOrmHandler(ormHandler, resultSet));
        }
        return targets;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return result
     */
    public static<Y> Y executeQueryFind(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
        OrmHandler<Y> ormHandler = new OrmHandler<>(targetClass);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        if (resultSet.next()) {
            return DataTableWrapper.updateOrmHandler(ormHandler, resultSet);
        }
        return null;
    }

    /**
     * update orm handler, store value of resultSet row
     * @param ormHandler the orm handler will be updated
     * @param resultSet the source of data value
     * @return  new target
     * @param <Y> target Class
     * @throws SQLException sql exception
     */
    protected static<Y> Y updateOrmHandler(OrmHandler<Y> ormHandler, ResultSet resultSet) throws SQLException {
        ormHandler.newTarget();
        for (String relationalKey : ormHandler.params.values()) {
            // result index
            int index;
            // find column, return index if it exists, else skip this column
            try {
                index = resultSet.findColumn(relationalKey);
            } catch (SQLException e) {
                continue;
            }
            ormHandler.setRelationalKeyValue(relationalKey, resultSet.getObject(index));
        }
        return ormHandler.getTarget();
    }

    /**
     * execute sql query once
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return result
     */
    public static<Y> Y executeQueryOneFieldFind(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
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
     * @throws SQLException sql execute exception
     * @return result
     */
    public static<Y> ArrayList<Y> executeQueryOneField(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) throws SQLException {
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
     * @throws SQLException sql execute exception
     * @return execute result or the key of the insert
     */
    public static int execute(String sqlString, SqlRunner sqlRunner) throws SQLException {
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        if (resultSet != null && resultSet.next()) return resultSet.getInt(1);
        return 0;
    }

}
