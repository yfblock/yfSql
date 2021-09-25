package Test;

import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

import java.util.ArrayList;

@DataRunner(username = "root", password = "root", database = "java-orm-test")
public class TestWrapper {

    @Select("select * from test")
    public ArrayList<Test> getTest() { return null;}

    @Select("select * from test where id < {0}")
    public ArrayList<Test> getTestIdLess(int id) { return null;}

    @Select("select id, test1, test3 from test where test1={0} and test2={1}")
    public Test login(String username, String password) {return null;}
}
