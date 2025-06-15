// src/router/index.js

import Vue from 'vue';
import VueRouter from 'vue-router';

// 引入布局和页面组件
import MainLayout from '../movie-user-home/MainLayout.vue';
import HomePage from '../movie-user-home/HomePage.vue';
import RankingPage from '../movie-user-home/RankingPage.vue';
import SearchPage from '../movie-user-home/SearchPage.vue'; // 1. 引入新组件


Vue.use(VueRouter);

const routes = [
    {
        // 根路径现在指向布局组件
        path: '/',
        component: MainLayout,
        // children 数组定义了哪些页面会在 MainLayout 的 <router-view> 中显示
        children: [
            {
                path: '', // 当访问'/'时，默认显示 HomePage
                name: 'Home',
                component: HomePage
            },
            {
                path: 'ranking', // 当访问'/ranking'时，显示 RankingPage
                name: 'Ranking',
                component: RankingPage
            },
            {
                path: 'search',
                name: 'Search',
                component: SearchPage
            }
        ]
    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
});

export default router;