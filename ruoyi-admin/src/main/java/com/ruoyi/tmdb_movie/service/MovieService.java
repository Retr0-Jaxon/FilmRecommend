package com.ruoyi.tmdb_movie.service;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.tmdb_movie.config.RestTemplateConfig;
import com.ruoyi.tmdb_movie.config.TmdbConfig;
import com.ruoyi.tmdb_movie.domain.Movie;
import com.ruoyi.tmdb_movie.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MovieService {
    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TmdbConfig tmdbConfig;

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

    public String getPosterUrlByMovieId(Long movieId) {
        String url = String.format("%s/movie/%d?api_key=%s",
                tmdbConfig.getBaseUrl(), movieId, tmdbConfig.getApiKey());

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            if (body != null && body.containsKey("poster_path")) {
                String posterPath = (String) body.get("poster_path");
                return "https://image.tmdb.org/t/p/w500" + posterPath; // TMDB 图片基准地址
            }
        }
        return null;
    }


    private final String MOVIE_CACHE_KEY = "movie:";


    public Movie getMovieById(Integer movieId) {
        String key = MOVIE_CACHE_KEY + movieId;

        // 从 Redis 中尝试获取
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            return JSON.parseObject(json, Movie.class);
        }

        // 不存在则从数据库查询
        Movie movie = movieMapper.getMovieById(movieId);
        if (movie != null) {
            // 存入 Redis，设置过期时间，例如 1 小时
            redisTemplate.opsForValue().set(key, JSON.toJSONString(movie), Duration.ofHours(1));
        }

        return movie;
    }
}