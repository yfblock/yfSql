package Test;

import io.github.yfblock.yfSql.Annotation.*;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

import java.util.ArrayList;

@DataRunner(runner = SqliteRunner.class, path = "test.db")
public class UserWrapper {

    @Select("select * from user")
    public ArrayList<User> getUsers() {return null;}

    @Select("select * from user where username={0} and password={1}")
    public User login(String username, String password) {return null;}

    @Insert("insert into user (username, password) VALUES ({0}, {1})")
    public Integer register(String username, String password) { return 0;}

    @Update("update user set qq={0} where id={1}")
    public void updateQQById(String qq, int id){}

    @Delete("delete from user where id={0}")
    public void deleteUserById(int id){}

    @Select("select * from user where balance > {0}")
    public User getUserByBalance(int balance) {return null;}
}
