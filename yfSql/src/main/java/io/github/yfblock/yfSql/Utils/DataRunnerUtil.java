package io.github.yfblock.yfSql.Utils;

public class DataRunnerUtil {
    public static<T> T getWrapper(Class<T> cls) {
        try {
            return (T) Class.forName("org.example.MainTest").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Not Found The Correct Class For The Interface named " + cls.getName());
            throw new RuntimeException(e);
        }
    }
}
