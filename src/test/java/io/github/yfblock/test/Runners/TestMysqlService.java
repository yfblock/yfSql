package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.New;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@DataRunner(database = "uav")
public class TestMysqlService {

//    private SqlRunner sqlRunner;

    @Select("select * from user")
    public List<User> getUsers() throws SQLException {
        return null;
    }

    @Select("select * from news")
    public List<New> getNews() throws SQLException {
        return null;
    }

    @Select("select count(*) as count from news")
    public Integer getCount() throws SQLException  {return null;}

    @Select("select title from news")
    public ArrayList<String> getTitles() throws SQLException { return null; }
}
