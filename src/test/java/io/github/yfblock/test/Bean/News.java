package io.github.yfblock.test.Bean;

import lombok.Data;

@Data
public class News {
    private Integer id;
    private String title;
    private String cover;
    private String summary;
    private String profile;
    private String createTime;
}
