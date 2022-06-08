package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.New;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@DataRunner(path = "test.db", runner = SqliteRunner.class)
public class TestService {

//    private SqlRunner sqlRunner;

    @Select("select id, username from user")
    public List<User> getUsers() throws SQLException {
        return null;
    }

    @Select("select * from news")
    public List<New> getNews() throws SQLException {
        return null;
    }

    @Select("select count(*) from user")
    public Integer getCount() throws SQLException {return null;}

    @Select("select title from news")
    public ArrayList<String> getTitles() throws SQLException { return null; }
}
