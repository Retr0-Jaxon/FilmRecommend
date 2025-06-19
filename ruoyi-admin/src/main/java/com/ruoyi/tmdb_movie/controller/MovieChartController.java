package com.ruoyi.tmdb_movie.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/charts")
public class MovieChartController {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @GetMapping("/movieVoteAnalysis")
    @ResponseBody
    public AjaxResult getMovieVoteAnalysis() {
        try {
            // 执行SQL查询
            String sql = "SELECT title AS name, vote_average, vote_count " +
                    "FROM tmdb_movies " +
                    "WHERE vote_count > 0 " +  // 过滤无效数据
                    "ORDER BY vote_count DESC " +
                    "LIMIT 50";  // 取投票量最高的50部电影

            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

            // 格式化数据（确保数字类型正确）
            List<Map<String, Object>> result = data.stream().map(item -> {
                Map<String, Object> formattedItem = new HashMap<>();
                formattedItem.put("name", item.get("name"));
                formattedItem.put("vote_average", Double.parseDouble(item.get("vote_average").toString()));
                formattedItem.put("vote_count", Integer.parseInt(item.get("vote_count").toString()));
                return formattedItem;
            }).collect(Collectors.toList());

            return AjaxResult.success(result);
        } catch (Exception e) {

            return AjaxResult.error("获取数据失败");
        }
    }

    @GetMapping("/movieGenreCloud")
    @ResponseBody
    public AjaxResult getMovieGenreCloud() {
        try {
            // 1. 获取所有电影的genres_str
            String sql = "SELECT genres_str FROM tmdb_movies WHERE genres_str IS NOT NULL";
            List<String> allGenres = jdbcTemplate.queryForList(sql, String.class);

            // 2. 统计类型出现频率
            Map<String, Integer> genreCount = new HashMap<>();
            for (String genresStr : allGenres) {
                if (StringUtils.isNotBlank(genresStr)) {
                    // 假设genres_str格式为"动作,冒险,科幻"
                    String[] genres = genresStr.split(",");
                    for (String genre : genres) {
                        genre = genre.trim();
                        if (!genre.isEmpty()) {
                            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                        }
                    }
                }
            }

            // 3. 转换为前端需要的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : genreCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("value", entry.getValue());
                result.add(item);
            }

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取数据失败");
        }
    }
}
