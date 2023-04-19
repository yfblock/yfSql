package com.example.beans;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.github.yfblock.yfSql.Sql.BasicDataSource;

@Component
public class DataSourceBuilder {
    
    @Bean
    public DataSource dataSource() {
        DataSource ds = new BasicDataSource("org.sqlite.JDBC", "jdbc:sqlite:test.db", "", "");
        System.out.println("dataSource inject" + ds);
        return ds;
    }
}
