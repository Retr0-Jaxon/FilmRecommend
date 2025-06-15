package com.ruoyi.tmdb_movie.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class Movie implements Serializable {
    private Long movieId;           // 电影ID
    private String overview;        // 剧情简介
    private Double popularity;      // 实时热度
    private String tagline;         // 宣传语
    private String title;           // 电影标题
    private Double voteAverage;     // 评分
    private Integer voteCount;      // 评分人数
    private String director;        // 导演
    private String countriesStr;    // 国家列表（字符串形式，如"US,CN"）
    private Double weeklyPopularity; // 周热度
    private Double monthlyPopularity; // 月热度
    private String genresStr;       // 类型列表（字符串形式，如"Action,Sci-Fi"）
    private Integer accessLevel;    // 访问权限级别

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCountriesStr() {
        return countriesStr;
    }

    public void setCountriesStr(String countriesStr) {
        this.countriesStr = countriesStr;
    }

    public Double getWeeklyPopularity() {
        return weeklyPopularity;
    }

    public void setWeeklyPopularity(Double weeklyPopularity) {
        this.weeklyPopularity = weeklyPopularity;
    }

    public Double getMonthlyPopularity() {
        return monthlyPopularity;
    }

    public void setMonthlyPopularity(Double monthlyPopularity) {
        this.monthlyPopularity = monthlyPopularity;
    }

    public String getGenresStr() {
        return genresStr;
    }

    public void setGenresStr(String genresStr) {
        this.genresStr = genresStr;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
}