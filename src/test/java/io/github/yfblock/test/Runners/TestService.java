package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.New;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;
import io.github.yfblock.yfSql.Runner.SqlRunner;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

import java.util.List;

@DataRunner(path = "test.db", runner = SqliteRunner.class)
public class TestService {

    private SqlRunner sqlRunner;

    @Select("select * from user")
    public List<User> getUsers() {
        return null;
    }

    @Select("select * from news")
    public List<New> getNews() {
        return null;
    }
}
