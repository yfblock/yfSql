package io.github.yfblock.yfSql.Sql;

import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Runner.SqlRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * DataTableWrapper can store
 *
 * @param <T> the database object to be use
 */
public class DataTableWrapper<T> {
    private String limitString = "";
    private String topString = "";
    private String table = "";
    private ArrayList<String> whereArray = new ArrayList<>();
    private final SqlRunner sqlRunner;
    private final OrmHandler<T> ormHandler;

    /**
     * * construct the wrapper
     *
     * @param targetClass the target will be set
     */
    public DataTableWrapper(Class<T> targetClass) {
        this(targetClass, new MysqlRunner());
    }

    /**
     * * construct the wrapper
     *
     * @param targetClass the target will be set
     * @param sqlRunner   the runner will used to query sql
     */
    public DataTableWrapper(Class<T> targetClass, SqlRunner sqlRunner) {
        this.ormHandler = new OrmHandler<>(targetClass);
        this.sqlRunner  = sqlRunner;
        this.setTable(ormHandler.getClassRelationalName());
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
     * * set database table
     *
     * @param table the table to be handled
     */
    public void setTable(String table) {
        this.table = "`" + table + "`";
    }

    /**
     * * set query condition
     * @param fieldName the database field name
     * @param value     the value will be search
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> where(String fieldName, String value) {
        return this.where(fieldName + "='" + value + "'");
    }

    /**
     * * set query condition
     *
     * @param fieldName the database field name
     * @param value     the value will be search
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> where(String fieldName, Object value) {
        return this.where(fieldName + '=' + String.valueOf(value));
    }

    /**
     * * set query condition
     *
     * @param condition the condition
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> where(String condition) {
        this.whereArray.add(condition);
        return this;
    }

    /**
     * * set query condition
     * @param conditions the conditions
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> where(Map<String, Object> conditions) {
        for (String condition : conditions.keySet())
            this.where(condition, conditions.get(condition));
        return this;
    }

    /**
     * * reset target
     */
    private void init() {
        this.topString = "";
        this.limitString = "";
        this.whereArray = new ArrayList<>();
    }

    /**
     * * get where string
     *
     * @return where string
     */
    private String getWhereString() {
        if(this.whereArray.size() == 0) return "";
        return "where " + String.join(" and ", this.whereArray);
    }

    /**
     * * get limit String
     * @return limit String
     */
    private String getLimitString() {
        if(this.limitString.equals("")) return "";
        return "limit " + this.limitString;
    }

    /**
     * * limit query
     * @param num the select number
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> limit(int num) {
        this.limitString = String.valueOf(num);
        return this;
    }

    /**
     * * limit query
     * @param start the select number start
     * @param end the select number end
     * @return this object deal with link operate
     */
    public DataTableWrapper<T> limit(int start, int end) {
        this.limitString = start + "," + end;
        return this;
    }

    /**
     * *  execute the select
     *
     * @return select result
     */
    public ArrayList<T> select() {
        String sqlTemplate = "select {0} {1} from {2} {3} {4}";    // select Tempate
        String sqlString = MessageFormat.format(sqlTemplate,
                this.topString, this.getColumnsString(), this.table, this.getWhereString(), this.getLimitString());
        return this.executeQuery(sqlString);
    }

    /**
     * * update database item
     * @param obj the object will be update
     */
    public void update(T obj) {
        ormHandler.setTarget(obj);
        // build update array
        ArrayList<String> updateValue = new ArrayList<>();
        for (String relationalKey : ormHandler.params.values()) {
            Object value = ormHandler.getRelationalKeyValue(relationalKey);
            if (value == null) continue;
            updateValue.add(relationalKey + "='" + value + "'");
        }
        // build query sql
        String sqlTemplate  = "update {0} set {1} {2}";
        String sqlString    = MessageFormat.format(sqlTemplate,
                this.table, String.join(",", updateValue), this.getWhereString());
        this.execute(sqlString);
    }

    /**
     * * add target to database
     *
     * @param obj the target will be added
     * @return the key of added
     */
    public int add(T obj) {
        ormHandler.setTarget(obj);

        // build columns and values
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        for (String relationalKey : ormHandler.params.values()) {
            Object value = ormHandler.getRelationalKeyValue(relationalKey);
            if (value == null) continue;
            columns.add(relationalKey);
            values.add("'" + value + "'");
        }

        // build sql
        String sqlTemplate = "insert into {0} ({1}) VALUES ({2})";
        String sqlString = MessageFormat.format(sqlTemplate,
                this.table, String.join(",", columns), String.join(",", values));
        return this.execute(sqlString);
    }

    /**
     * * query delete
     */
    public void delete() {
        String sqlTemplate  = "delete from {0} {1}";
        String sqlString    = MessageFormat.format(sqlTemplate, this.table, this.getWhereString());
        this.execute(sqlString);
    }

    /**
     * execute sql query
     * @param sqlString sql string will be query
     * @return query results
     */
    public ArrayList<T> executeQuery(String sqlString) {
        try {
            this.init();
            System.out.println("executeQuery: " + sqlString);
            ResultSet resultSet = this.sqlRunner.executeQuery(sqlString);
            ArrayList<T> targets = new ArrayList<>();
            while (resultSet.next()) {
                ormHandler.newTarget();
                for (String relationalKey : ormHandler.params.values()) {
                    ormHandler.setRelationalKeyValue(relationalKey, resultSet.getObject(relationalKey));
                }
                targets.add(ormHandler.getTarget());
            }
            return targets;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * execute sql query
     * @param sqlString sql string will be query
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return results
     */
    public static<Y> ArrayList<Y> executeQuery(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) {
        try {
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * execute sql query one time
     * @param sqlString sql string will be query
     * @param targetClass result type
     * @param sqlRunner sqlRunner
     * @param <Y> targetType
     * @return result
     */
    public static<Y> Y executeQueryFind(String sqlString, Class<Y> targetClass, SqlRunner sqlRunner) {
        OrmHandler<Y> ormHandler = new OrmHandler<>(targetClass);
        System.out.println("executeQuery: " + sqlString);
        ResultSet resultSet = sqlRunner.executeQuery(sqlString);
        try {
            if (resultSet.next()) {
                ormHandler.newTarget();
                for (String relationalKey : ormHandler.params.values()) {
                    try{
                        ormHandler.setRelationalKeyValue(relationalKey, resultSet.getObject(relationalKey));
                    } catch (SQLException ignored) { }
                }
                return ormHandler.getTarget();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * execute sql, if it is insert, then will return the primary key of the object has been inserted
     * @param sqlString the sql will be executed
     * @return execute result or the key of the insert
     */
    public int execute(String sqlString) {
        try {
            this.init();
            System.out.println("execute: " + sqlString);
            ResultSet resultSet = sqlRunner.executeQuery(sqlString);
            if (resultSet != null && resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    /**
     * get columns
     * @return columns string
     */
    protected String getColumnsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String column : ormHandler.params.values()) {
            stringBuilder.append(',');
            stringBuilder.append(column);
        }
        return stringBuilder.substring(1);
    }

    /**
     * select top number of data, always used by sqlserver
     *
     * @param num number of selected
     * @return object of this
     */
    public DataTableWrapper<T> top(int num) {
        this.topString = "TOP(" + num + ")";
        return this;
    }

    /**
     * select top number of data, always used by sqlserver
     *
     * @param num number of used
     * @return object of this
     */
    public DataTableWrapper<T> top(String num) {
        this.topString = "TOP(" + num + ")";
        return this;
    }

    /**
     * get number of records
     * @return the number of records
     */
    public int count() {
        String sqlTemplate  = "select {0} count(*) from {1} {2} {3}";
        String sqlString    = MessageFormat.format(sqlTemplate,
                this.topString, this.table, this.getWhereString(), this.getLimitString());
        this.init();
        return this.execute(sqlString);
    }

    /**
     * single query
     * @return single record
     */
    public T find() {
        this.limit(1);
        ArrayList<T> results = this.select();
        if (results.size() == 0) return null;
        return results.get(0);
    }

}
