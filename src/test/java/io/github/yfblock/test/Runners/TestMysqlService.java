package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.New;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

import java.util.List;

@DataRunner(database = "uav")
public class TestMysqlService {

//    private SqlRunner sqlRunner;

    @Select("select * from user")
    public List<User> getUsers() {
        return null;
    }

    @Select("select * from news")
    public List<New> getNews() {
        return null;
    }
}
