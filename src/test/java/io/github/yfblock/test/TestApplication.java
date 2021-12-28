package io.github.yfblock.test;

import io.github.yfblock.test.Bean.New;
import io.github.yfblock.test.Runners.TestMysqlService;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestApplication {
    @Test
    public void testConnection() {
//        mysqlRunner.executeQuery("select * from news");
        TestMysqlService testMysqlService = new TestMysqlService();
        List<New> news = testMysqlService.getNews();
        for(New n : news) {
            System.out.println(n);
        }
//        TestService testService = new TestService();
//        List<User> users = testService.getUsers();
//        System.out.println(users);


    }
}
