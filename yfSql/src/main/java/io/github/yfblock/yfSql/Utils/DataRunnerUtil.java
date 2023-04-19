package io.github.yfblock.yfSql.Utils;

import java.lang.reflect.InvocationTargetException;

import javax.sql.DataSource;

/**
 * @author yufeng
 */
public class DataRunnerUtil {
    public static<T> T getWrapper(Class<T> cls) {
        try {
            Class<?> clz = Class.forName(cls.getTypeName() + "Impl");
            return (T) clz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            System.out.println("Not Found The Correct Class For The Interface named " + cls.getName());
            throw new RuntimeException(e);
        }
    }

    public static<T> T getWrapper(Class<T> cls, DataSource dataSource) {
        try {
            Class<?> clz = Class.forName(cls.getTypeName() + "Impl");
            return (T) clz.getDeclaredConstructor(DataSource.class).newInstance(dataSource);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            System.out.println("Not Found The Correct Class For The Interface named " + cls.getName());
            throw new RuntimeException(e);
        }
    }
}
