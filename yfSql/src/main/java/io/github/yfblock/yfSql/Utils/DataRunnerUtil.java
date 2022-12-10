package io.github.yfblock.yfSql.Utils;

import io.github.yfblock.yfSql.Runner.SqlRunner;

import java.lang.reflect.InvocationTargetException;

public class DataRunnerUtil {
    public static<T> T getWrapper(Class<T> cls) {
        try {
            return (T) Class.forName(cls.getTypeName() + "Impl").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Not Found The Correct Class For The Interface named " + cls.getName());
            throw new RuntimeException(e);
        }
    }

    public static<T> T getWrapper(Class<T> cls, SqlRunner sqlRunner) {
        try {
            Class<?> clz = Class.forName(cls.getTypeName() + "Impl");
            return (T) clz.getDeclaredConstructor(SqlRunner.class).newInstance(sqlRunner);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException e) {
            System.out.println("Not Found The Correct Class For The Interface named " + cls.getName());
            throw new RuntimeException(e);
        }
    }
}
