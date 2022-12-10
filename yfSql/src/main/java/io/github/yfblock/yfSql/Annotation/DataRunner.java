package io.github.yfblock.yfSql.Annotation;

import io.github.yfblock.yfSql.Runner.SqlRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DataRunner {
    Class<? extends SqlRunner> value() default SqlRunner.class;
}
