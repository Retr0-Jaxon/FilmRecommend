package com.ruoyi.tmdb_movie.service;

import com.ruoyi.tmdb_movie.domain.Movie;
import com.ruoyi.tmdb_movie.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MovieService {
    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String REDIS_KEY = "movies:all";

    public List<Movie> getAllMovies() {
        // 1. 检查Redis缓存
        List<Movie> movies = (List<Movie>) redisTemplate.opsForValue().get(REDIS_KEY);

        if (movies != null) {
            return movies;
        }

        // 2. 查询数据库
        movies = movieMapper.selectAllMovies();

        // 3. 存入Redis（缓存1小时）
        redisTemplate.opsForValue().set(REDIS_KEY, movies, 1, TimeUnit.HOURS);

        return movies;
    }

    // 清理缓存（在数据变更时调用）
    public void clearMovieCache() {
        redisTemplate.delete(REDIS_KEY);
    }
}