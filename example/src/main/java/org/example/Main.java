package org.example;

import io.github.yfblock.yfSql.Sql.BasicDataSource;
import io.github.yfblock.yfSql.Sql.DataTableWrapper;
import io.github.yfblock.yfSql.Utils.DataRunnerUtil;
import org.example.Bean.User;
import org.example.Runners.TestServiceInterface;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.sql.DataSource;

public class Main {
    public static DataSource dataSource;
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        dataSource = new BasicDataSource("org.sqlite.JDBC", "jdbc:sqlite:test.db", "", "");
        Main.testForPassSqlRunner();
        System.out.println();
        Main.testForConstructorRunner();
    }

    public static void testForConstructorRunner() throws SQLException {
        System.out.println("---------------------- TEST CONSTRUCTOR RUNNER ----------------------");
        TestServiceInterface test = DataRunnerUtil.getWrapper(TestServiceInterface.class, Main.dataSource);
        // 测试插入
        test.insertUser("admin2", "admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        // 测试修改
        test.updateUser("adminUpdate2", "admin2");
        // 测试 find 查询
        User findUser = test.getUser("admin2");
        System.out.println(findUser);
        // 测试删除
        test.deleteUser("admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        System.out.println("-------------------- TEST CONSTRUCTOR RUNNER END --------------------");
    }

    public static void testForPassSqlRunner() throws SQLException {
        System.out.println("---------------------- TEST PASS RUNNER RUNNER ----------------------");
        TestServiceInterface test = DataRunnerUtil.getWrapper(TestServiceInterface.class, Main.dataSource);
        // 测试插入
        test.insertUser("admin2", "admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        // 测试修改
        test.updateUser("adminUpdate2", "admin2");
        // 测试 find 查询
        User findUser = test.getUser("admin2");
        System.out.println(findUser);
        // 测试删除
        test.deleteUser("admin2");
        // 测试 select 查询
        for(User user : test.getUsers()) {
            System.out.println(user);
        }
        System.out.println("-------------------- TEST PASS RUNNER RUNNER END --------------------");
    }
}