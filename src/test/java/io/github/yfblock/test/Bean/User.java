package io.github.yfblock.test.Bean;

import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Runner.SqliteRunner;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private Integer username;
    private Integer password;
}
