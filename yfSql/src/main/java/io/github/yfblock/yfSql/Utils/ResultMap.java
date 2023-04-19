package io.github.yfblock.yfSql.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Result Map
 * Contains the setters and getters in the class
 * @author yufeng
 */
public class ResultMap {
    /**
     * setters, contains the setters of the fields in the class.
     * use field name as key.
     */
    public Map<Field, Method> setters = new HashMap<>();
    /**
     * getters, contains the getters of the fields in the class.
     * use field name as key.
     */
    public Map<Field, Method> getters = new HashMap<>();
    /**
     * fields map, use field in the class as the key.
     * store column as the value.
     */
    public Map<Field, String> fieldsMap = new HashMap<>();
    /**
     * columns map, use field in the class as the key.
     * store column as the value.
     */
    public Map<String, Field> columnsMap = new HashMap<>();
}