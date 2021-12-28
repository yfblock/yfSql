package io.github.yfblock.test;

import io.github.yfblock.test.Bean.News;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.test.Runners.TestMysqlService;
import io.github.yfblock.test.Runners.TestService;
import io.github.yfblock.yfSql.Runner.MysqlRunner;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestApplication {
    @Test
    public void testConnection() {
//        MysqlRunner mysqlRunner = new MysqlRunner("root", "root", "uav");
//        mysqlRunner.executeQuery("select * from news");
        TestMysqlService testMysqlService = new TestMysqlService();
        List<News> news = testMysqlService.getNews();
        System.out.println(news);
//        TestService testService = new TestService();
//        List<User> users = testService.getUsers();
//        System.out.println(users);


    }
}
