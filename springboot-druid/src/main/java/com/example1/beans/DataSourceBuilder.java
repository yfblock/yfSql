package com.example1.beans;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import io.github.yfblock.yfSql.Sql.BasicDataSource;

@Component
public class DataSourceBuilder {
    
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        // DataSource ds = new BasicDataSource("org.sqlite.JDBC", "jdbc:sqlite:test.db", "", "");
        // System.out.println("dataSource inject" + ds);
        // return ds;
        DruidDataSource druidDataSource = new DruidDataSource();
        //druidDataSource.setUrl();
        //druidDataSource.setUsername();
        //druidDataSource.setPassword();
        return druidDataSource;
    }
}
