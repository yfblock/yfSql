package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.News;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

import java.util.List;

@DataRunner(path = "test.db",runner = SqliteRunner.class)
public class TestService {

    @Select("select * from user")
    public List<User> getUsers() {
        return null;
    }

    @Select("select * from news")
    public List<News> getNews() {
        return null;
    }
}
