package Test;

import io.github.yfblock.yfSql.Annotation.Delete;
import io.github.yfblock.yfSql.Annotation.Insert;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

@DataRunner(username = "root", password = "root", database = "older")
public class UserWrapper {

    @Select("select * from user where username={0} and password={1}")
    public User login(String username, String password) {return null;}

    @Insert("insert into user (username, password) VALUES ({0}, {1})")
    public int register(String username, String password) { return 0;}

    @Delete("delete from user where id={0}")
    public void deleteUserById(int id){}
}
