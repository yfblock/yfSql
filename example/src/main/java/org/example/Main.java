package org.example;

import io.github.yfblock.yfSql.Runner.SqliteRunner;
import io.github.yfblock.yfSql.Utils.DataRunnerUtil;
import org.example.Bean.User;
import org.example.Runners.TestServiceInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

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
        SqliteRunner sqliteRunner = new SqliteRunner("test.db");
        TestServiceInterface test = DataRunnerUtil.getWrapper(TestServiceInterface.class, sqliteRunner);
        // 测试插入
        test.insertUser("admin2", "admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        // 测试修改
        test.updateUser("adminUpdate2", "admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        // 测试删除
        test.deleteUser("admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
    }
}