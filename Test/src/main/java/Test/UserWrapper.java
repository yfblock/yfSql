package Test;

import io.github.yfblock.yfSql.Annotation.*;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

@DataRunner(path = "test.db", runner = SqliteRunner.class)
public class UserWrapper {

    @Select("select * from user where username={0} and password={1}")
    public User login(String username, String password) {return null;}

    @Insert("insert into user (username, password) VALUES ({0}, {1})")
    public int register(String username, String password) { return 0;}

    @Update("update user set qq={0} where id={1}")
    public void updateQQById(String qq, int id){}

    @Delete("delete from user where id={0}")
    public void deleteUserById(int id){}
}
