package io.github.yfblock.yfSql.Sql;

import io.github.yfblock.yfSql.Annotation.DataField;
import io.github.yfblock.yfSql.Annotation.DataTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrmHandler<T> {
    protected Class<T> targetClass;
    protected T target;
    protected Map<String, String> params = new HashMap<>();             // store database field name, key is class field name
    protected Map<String, String> relationalParams = new HashMap<>();   // store class field name, key is database field name
    protected Map<String, Method> paramGet = new HashMap<>();           // store get method object, key is class field name
    protected Map<String, Method> paramSet = new HashMap<>();           // store set method object, key is class field name

    /**
     * * constructor, store get methods and set methods
     * @param targetClass the target will be built
     */
    public OrmHandler(Class<T> targetClass) {
        try {
            this.targetClass = targetClass;
            for(Field field : targetClass.getDeclaredFields()) {
                String targetName = this.getFieldRelationalName(field);
                params.put(field.getName(), targetName);
                relationalParams.put(targetName, field.getName());
                paramGet.put(field.getName(), targetClass.getDeclaredMethod(this.getGetterName(field.getName())));
                paramSet.put(field.getName(), targetClass.getDeclaredMethod(this.getSetterName(field.getName()), field.getType()));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    /**
     * * constructor, store get methods and set methods
     * @param targetClass the target will be built
     * @param target the target
     */
    public OrmHandler(Class<T> targetClass, T target) {
        this(targetClass);
        this.target = target;
    }

    /**
     * * get the name of the database field corresponding to the property
     * @param field the property name
     * @return database field name
     */
    public String getFieldRelationalName(Field field) {
        StringBuilder stringBuilder = new StringBuilder();
        // if it has dataField annotation then return annotation value
        DataField dataField = field.getAnnotation(DataField.class);
        if(dataField != null) return dataField.value();
        // else return value according to DataField
        return field.getName();
    }

    /**
     * * get getter method name
     * @param paramName property name
     * @return getter method name
     */
    private String getGetterName(String paramName) {
        return "get" + Character.toUpperCase(paramName.charAt(0)) + paramName.substring(1);
    }

    /**
     * * get setter method name
     * @param paramName property name
     * @return setter method name
     */
    private String getSetterName(String paramName) {
        return "set" + Character.toUpperCase(paramName.charAt(0)) + paramName.substring(1);
    }

    /**
     * * get property value
     * @param key the property value will be got
     * @return property value
     */
    public Object getKeyValue(String key) {
        try {
            Method getter = this.paramGet.get(key);
            if(getter == null) return null;
            return getter.invoke(this.target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * * get property value by database field name
     * @param relationalKey database field name
     * @return property value
     */
    public Object getRelationalKeyValue(String relationalKey) {
        return this.getKeyValue(this.relationalParams.get(relationalKey));
    }

    /**
     * * set property value
     * @param key property name
     * @param value the value will be set
     */
    public void setKeyValue(String key, Object value) {
        try {
            Method setter = this.paramSet.get(key);
            if(setter == null) return;
            if(value != null && value.getClass() == BigDecimal.class) {
                setter.invoke(this.target, ((BigDecimal) value).doubleValue());
            } else {
                setter.invoke(this.target, value);
            }
        }catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * * set property value by database field name
     * @param relationalKey database field name
     * @param value the value will be set
     */
    public void setRelationalKeyValue(String relationalKey, Object value) {
        this.setKeyValue(this.relationalParams.get(relationalKey), value);
    }

    /**
     * * create a new target to store value and transfer
     */
    public void newTarget() {
        try {
            this.target = this.targetClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * * Get the property value in string type
     * @param key the property name
     * @return the property value in string type
     */
    public String getKeyValueString(String key) {
        return String.valueOf(this.getKeyValue(key));
    }

    /**
     * get database name corresponding to class
     * @return the database name corresponding to class
     */
    public String getClassRelationalName() {
        // if it has DataTable annotation, then return the annotation value
        DataTable dataTable = this.targetClass.getAnnotation(DataTable.class);
        if(dataTable != null) return dataTable.value();
        // else return the class name
        return this.targetClass.getSimpleName();
    }
}
