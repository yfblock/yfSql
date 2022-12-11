package org.example.Runners;

import io.github.yfblock.yfSql.Annotation.*;
import org.example.Bean.User;
import org.example.DatabaseConfig;

import java.sql.SQLException;
import java.util.List;

@DataRunner
public interface TestServiceInterface {

    @Select("select id, username, password from user")
    public List<User> getUsers() throws SQLException;

    @Insert("insert into user (username, password) VALUES (\"{0}\", \"{1}\")")
    public int insertUser(String username, String password) throws SQLException;

    @Update("update user set password = \"{0}\" where username = \"{1}\"")
    public void updateUser(String password, String username) throws SQLException;

    @Delete("delete from user where username = \"{0}\"")
    public void deleteUser(String username) throws SQLException;
}