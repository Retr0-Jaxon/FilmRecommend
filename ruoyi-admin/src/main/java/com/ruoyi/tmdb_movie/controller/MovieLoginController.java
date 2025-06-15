package com.ruoyi.tmdb_movie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieLoginController {

    @GetMapping("/movieLogin")
    public String movieLogin() {
        return "redirect:/login"; // 假设登录界面的路径是 /login
    }
}
