package com.ruoyi.tmdb_movie.controller;

import com.ruoyi.tmdb_movie.domain.Movie;
import com.ruoyi.tmdb_movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController

@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/{movieId}/poster")
    public String getPosterUrl(@PathVariable Integer movieId) {
        String cacheKey = "movie:poster:" + movieId;
        String posterUrl = redisTemplate.opsForValue().get(cacheKey);

        if (posterUrl == null) {
            posterUrl = movieService.getPosterUrlByMovieId(movieId);
            if (posterUrl != null) {
                redisTemplate.opsForValue().set(cacheKey, posterUrl, 1, TimeUnit.DAYS);
            } else {
                return "Not found";
            }
        }

        return posterUrl;
    }
}
