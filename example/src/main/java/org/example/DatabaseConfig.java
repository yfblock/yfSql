package org.example;

import io.github.yfblock.yfSql.Annotation.DatabaseConnection;
import io.github.yfblock.yfSql.Runner.SqlRunner;

@DatabaseConnection
public class DatabaseConfig extends SqlRunner {
    /**
     * SqliteRunner Constructor, build a sqlite runner
     */
    public DatabaseConfig() {
        super("org.sqlite.JDBC", "jdbc:sqlite:test.db", "", "");
    }
}
