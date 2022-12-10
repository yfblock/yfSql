package org.example;

import io.github.yfblock.yfSql.Utils.DataRunnerUtil;
import org.example.Bean.User;
import org.example.Runners.TestService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        System.out.println("Hello world!");
//        TestService testService = new TestService();
//        List<User> users = testService.getUsers();
//        for(User user: users) {
//            System.out.println(user);
//        }

//        TestInterface test = (TestInterface) Class.forName("org.example.MainTest").newInstance();
//        test.test();
        TestInterface test = DataRunnerUtil.getWrapper(TestInterface.class);
        test.test();
    }
}