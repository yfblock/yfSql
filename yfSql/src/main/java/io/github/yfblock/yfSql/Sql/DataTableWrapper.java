package io.github.yfblock.yfSql.Sql;

import io.github.yfblock.yfSql.Annotation.DataField;
import io.github.yfblock.yfSql.Utils.ResultMap;
import io.github.yfblock.yfSql.Utils.ResultMapCollection;
import io.github.yfblock.yfSql.Utils.StringUtil;
import jakarta.annotation.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;

/**
 * DataTableWrapper can store
 * @author yufeng
 */
@SuppressWarnings({"executeQueryOneFieldFind"})
public abstract class DataTableWrapper {

    @Resource
    protected DataSource dataSource;

    /**
     * execute sql query
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return results
     */
    public <Y> ArrayList<Y> executeQuery(String sqlString, Class<Y> targetClass) throws SQLException {
        ResultSet resultSet = this.executeQuery(sqlString);
        ArrayList<Y> targets = new ArrayList<>();
        while (resultSet.next()) {
            targets.add(this.buildTarget(targetClass, resultSet));
        }
        return targets;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return result
     */
    public <Y> Y executeQueryFind(String sqlString, Class<Y> targetClass) throws SQLException {
        ResultSet resultSet = this.executeQuery(sqlString);
        if (resultSet.next()) {
            return this.buildTarget(targetClass, resultSet);
        }
        return null;
    }

    /**
     * execute sql query once
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return result
     */
    public <Y> Y executeQueryOneFieldFind(String sqlString, Class<Y> targetClass) throws SQLException {
        ResultSet resultSet = this.executeQuery(sqlString);
        // Return if there is data
        if (resultSet.next()) {
            return resultSet.getObject(1, targetClass);
        }
        return null;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be queried
     * @param targetClass result type
     * @param <Y> targetType
     * @throws SQLException sql execute exception
     * @return result
     */
    public <Y> ArrayList<Y> executeQueryOneField(String sqlString, Class<Y> targetClass) throws SQLException {
        ResultSet resultSet = this.executeQuery(sqlString);
        ArrayList<Y> targets = new ArrayList<>();
        while (resultSet.next()) {
            targets.add(resultSet.getObject(1, targetClass));
        }
        return targets;
    }


    /**
     * execute sql, if it is insert, then will return the primary key of the object has been inserted
     * @param sqlString the sql will be executed
     * @throws SQLException sql execute exception
     * @return execute result or the key of the insert
     */
    protected int execute(String sqlString) throws SQLException {
        ResultSet resultSet = this.executeQuery(sqlString);
        if (resultSet != null && resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

        /**
     * build target class
     * @param targetClass Class<Y> targetClass
     * @param resultSet ResultSet returnSet
     */
    protected <Y> Y buildTarget(Class<Y> targetClass, ResultSet resultSet) throws SQLException {
        try {
            Y target = targetClass.getDeclaredConstructor().newInstance();

            ResultMap resultMap = ResultMapCollection.maps.get(targetClass.getName());
            if(resultMap == null) {
                resultMap = new ResultMap();
                for(Field field : targetClass.getDeclaredFields()) {
                    DataField dataField = field.getAnnotation(DataField.class);

                    String columnName;
                    if(dataField != null) {
                        columnName = dataField.value();
                    } else {
                        columnName = StringUtil.toUnderlineCase(field.getName());
                    }

                    resultMap.fieldsMap.put(field, columnName);
                    resultMap.columnsMap.put(columnName, field);

                    String methodFieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

                    Method setter = targetClass.getDeclaredMethod("set" + methodFieldName, field.getType());
                    Method getter = targetClass.getDeclaredMethod("get" + methodFieldName);
                    
                    resultMap.getters.put(field, getter);
                    resultMap.setters.put(field, setter);
                }
                ResultMapCollection.maps.put(targetClass.getName(), resultMap);
            }
            
            for (Field field : resultMap.fieldsMap.keySet()) {
                Object obj = resultSet.getObject(resultMap.fieldsMap.get(field));
                resultMap.setters.get(field).invoke(target, obj);
            }

            return target;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute sql
     * @param sqlString sql
     * @return the result
     * @throws SQLException throws sql exception
     */
    protected ResultSet executeQuery(String sqlString) throws SQLException {
        Connection conn = this.dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.execute();
        if (sqlString.trim().indexOf("insert") == 0) {
            return preparedStatement.getGeneratedKeys();
        } else {
            return preparedStatement.getResultSet();
        }
    }

}
