package org.example;

import org.example.Bean.User;
import org.example.Runners.TestService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Hello world!");
        TestService testService = new TestService();
        List<User> users = testService.getUsers();
        for(User user: users) {
            System.out.println(user);
        }

    }
}