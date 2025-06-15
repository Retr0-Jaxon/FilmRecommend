package com.ruoyi.tmdb_movie.mapper;

import com.ruoyi.tmdb_movie.domain.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    List<Movie> selectAllMovies();

    @Select("SELECT * FROM tmdb_movies WHERE movie_id = #{movieId}")
    Movie getMovieById(@Param("movieId") Integer movieId);
}