package io.github.yfblock.yfSql.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DataRunner {
    boolean useProperty() default false;
    String password() default "root";
    String username() default "root";
    String database() default "database";
    String hostname() default "localhost";
    String port() default "3306";
}
