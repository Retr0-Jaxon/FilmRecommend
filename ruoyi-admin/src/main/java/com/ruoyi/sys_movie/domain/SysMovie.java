package com.ruoyi.sys_movie.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电影信息对象 sys_movie
 * 
 * @author ruoyi
 * @date 2025-05-23
 */
public class SysMovie extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 电影ID */
    private Long movieId;

    /** 电影标题 */
    @Excel(name = "电影标题")
    private String title;

    /** 电影类型(多个类型用|分隔) */
    @Excel(name = "电影类型(多个类型用|分隔)")
    private String genres;

    /** IMDb ID */
    @Excel(name = "IMDb ID")
    private String imdbId;

    /** TMDB ID */
    @Excel(name = "TMDB ID")
    private String tmdbId;

    public void setMovieId(Long movieId) 
    {
        this.movieId = movieId;
    }

    public Long getMovieId() 
    {
        return movieId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setGenres(String genres) 
    {
        this.genres = genres;
    }

    public String getGenres() 
    {
        return genres;
    }

    public void setImdbId(String imdbId) 
    {
        this.imdbId = imdbId;
    }

    public String getImdbId() 
    {
        return imdbId;
    }

    public void setTmdbId(String tmdbId) 
    {
        this.tmdbId = tmdbId;
    }

    public String getTmdbId() 
    {
        return tmdbId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("movieId", getMovieId())
            .append("title", getTitle())
            .append("genres", getGenres())
            .append("imdbId", getImdbId())
            .append("tmdbId", getTmdbId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
