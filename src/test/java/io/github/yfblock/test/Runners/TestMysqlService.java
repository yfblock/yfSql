package io.github.yfblock.test.Runners;

import io.github.yfblock.test.Bean.News;
import io.github.yfblock.test.Bean.User;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Select;

import java.util.List;

@DataRunner()
public class TestMysqlService {

    @Select("select * from user")
    public List<User> getUsers() {
        return null;
    }

    @Select("select * from news")
    public List<News> getNews() {
        return null;
    }
}
