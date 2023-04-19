package com.example1;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example1.Bean.User;
import com.example1.Runners.TestServiceInterface;

import jakarta.annotation.Resource;

@Component
@Order(value = 1)
public class StartRunnerOne implements CommandLineRunner {

    @Resource
    DataSource dataSource;

    @Resource
    TestServiceInterface testServiceInterface;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>服务启动第一个开始执行的任务<<<<");
        System.out.println(dataSource);
        for(User user : testServiceInterface.getUsers()) {
            System.out.println(user);
        }
    }
}