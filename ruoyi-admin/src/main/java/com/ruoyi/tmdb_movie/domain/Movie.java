package com.ruoyi.tmdb_movie.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class Movie implements Serializable {
    private Integer movie_id;           // 电影ID
    private String overview;        // 剧情简介
    private Double popularity;      // 实时热度
    private String tagline;         // 宣传语
    private String title;           // 电影标题
    private Double vote_Average;     // 评分
    private Integer vote_Count;      // 评分人数
    private String director;        // 导演
    private String countries_Str;    // 国家列表（字符串形式，如"US,CN"）
    private Double weekly_Popularity; // 周热度
    private Double monthly_Popularity; // 月热度
    private String genres_Str;       // 类型列表（字符串形式，如"Action,Sci-Fi"）
    private Integer access_Level;    // 访问权限级别
}