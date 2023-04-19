package com.example1.Bean;

import lombok.Data;

import java.time.LocalDateTime;

@Data
//@DataTable("news")
public class New {
    private Integer id;
    private String title;
    private String cover;
    private String summary;
    private String profile;
    private LocalDateTime createTime;
}