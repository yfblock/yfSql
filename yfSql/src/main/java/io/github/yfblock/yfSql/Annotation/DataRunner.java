package io.github.yfblock.yfSql.Annotation;

import io.github.yfblock.yfSql.Runner.MysqlRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DataRunner {
    boolean useProperty() default false;
    String password()   default "root";
    String username()   default "root";
    String database()   default "";
    String hostname()   default "localhost";
    String port()       default "3306";
    String path()       default "";
    Class<?> runner()   default MysqlRunner.class;
}
