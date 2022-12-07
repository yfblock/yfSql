package org.example.Runners;

import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;
import io.github.yfblock.yfSql.Runner.SqlRunner;
import io.github.yfblock.yfSql.Runner.SqliteRunner;
import org.example.Bean.New;
import org.example.Bean.User;

import java.sql.SQLException;
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
}