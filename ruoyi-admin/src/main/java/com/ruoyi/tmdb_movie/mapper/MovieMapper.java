package com.ruoyi.tmdb_movie.mapper;

import com.ruoyi.tmdb_movie.domain.Movie;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MovieMapper {
    List<Movie> selectAllMovies();
}