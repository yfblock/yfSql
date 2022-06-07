package io.github.yfblock.test.Bean;

import io.github.yfblock.yfSql.Annotation.DataField;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;

    // 支持DataField
    @DataField("password")
    private String userPassword;
}
