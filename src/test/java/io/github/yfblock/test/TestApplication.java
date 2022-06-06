package io.github.yfblock.test;

import io.github.yfblock.test.Bean.User;
import io.github.yfblock.test.Runners.TestService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class TestApplication {
    @Test
    public void testConnection() throws SQLException {
//        TestService testService = new TestService();
//        try {
//            List<User> users = testService.getUsers();
//            for(User n : users) {
//                System.out.println(n);
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        System.out.println(testService.getCount());

        try {
            this.testException();
        } catch (SQLException e) {
            System.out.println("捕捉到异常");
            e.printStackTrace();
        }
    }

    public void testException() throws SQLException {
        TestService testService = new TestService();
        try {
            List<User> users = testService.getUsers();
            for(User n : users) {
                System.out.println(n);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
