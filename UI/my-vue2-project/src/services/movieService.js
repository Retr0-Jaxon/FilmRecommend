// src/services/movieService.js

// 模拟一个更丰富的电影数据库
// 增加了 rating(评分), region(地区), playCount(播放量), genre(类型)
const db = [
    { id: 1, title: 'Dune: Part Two', posterUrl: 'https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05ESeaB.jpg', isVip: true, releaseDate: '2024-02-27', playCount: 8500000, genre: 'scifi', region: 'USA', rating: 8.8 },
    { id: 2, title: 'Oppenheimer', posterUrl: 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', isVip: true, releaseDate: '2023-07-19', playCount: 12000000, genre: 'suspense', region: 'USA', rating: 8.6 },
    { id: 3, title: 'Your Name.', posterUrl: 'https://image.tmdb.org/t/p/w500/xq1Ugd62d23K2knRUx6xxuALTZB.jpg', isVip: false, releaseDate: '2016-08-26', playCount: 25000000, genre: 'romance', region: 'Japan', rating: 8.9 },
    { id: 4, title: 'The Dark Knight', posterUrl: 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', isVip: true, releaseDate: '2008-07-16', playCount: 35000000, genre: 'action', region: 'USA', rating: 9.0 },
    { id: 5, title: 'Inception', posterUrl: 'https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq27gApcjBsovLgaq.jpg', isVip: false, releaseDate: '2010-07-15', playCount: 32000000, genre: 'scifi', region: 'USA', rating: 8.7 },
    { id: 6, title: 'La La Land', posterUrl: 'https://image.tmdb.org/t/p/w500/uDO8zWDhfWwoFdEJhQcunderlineAXv.jpg', isVip: false, releaseDate: '2016-12-09', playCount: 18000000, genre: 'romance', region: 'USA', rating: 7.9 },
    { id: 7, title: 'The Silence of the Lambs', posterUrl: 'https://image.tmdb.org/t/p/w500/uS9m8fHn31aR6fn6Q7nd2vRwsA5.jpg', isVip: true, releaseDate: '1991-02-01', playCount: 28000000, genre: 'suspense', region: 'USA', rating: 8.6 },
    { id: 8, 'title': 'John Wick: Chapter 4', posterUrl: 'https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg', isVip: true, releaseDate: '2023-03-22', playCount: 15000000, genre: 'action', region: 'USA', rating: 7.8 },
    { id: 9, 'title': 'Interstellar', posterUrl: 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', isVip: false, releaseDate: '2014-11-05', playCount: 41000000, genre: 'scifi', region: 'USA', rating: 8.6 },
    { id: 10, 'title': 'Parasite', posterUrl: 'https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', isVip: false, releaseDate: '2019-05-30', playCount: 22000000, genre: 'suspense', region: 'Korea', rating: 8.5 },
    { id: 11, 'title': 'Spirited Away', posterUrl: 'https://image.tmdb.org/t/p/w500/39wmItIW2asIos3gQp22dO2gK0g.jpg', isVip: false, releaseDate: '2001-07-20', playCount: 30000000, genre: 'romance', region: 'Japan', rating: 8.5 },
];

// 模拟 API 调用
export const movieService = {
    // 获取所有电影
    getAllMovies() {
        // 返回一个 Promise，模拟异步获取数据
        return new Promise(resolve => {
            setTimeout(() => {
                resolve([...db]); // 返回数据库的副本，防止原始数据被意外修改
            }, 200); // 模拟 200ms 的网络延迟
        });
    },

    // 获取所有可用类型
    getGenres() {
        return [
            { key: 'all', name: '全部' },
            { key: 'romance', name: '恋爱' },
            { key: 'suspense', name: '悬疑' },
            { key: 'scifi', name: '科幻' },
            { key: 'action', name: '动作' },
        ];
    },

    // 获取所有可用地区
    getRegions() {
        return [
            { key: 'all', name: '全部' },
            { key: 'USA', name: '美国' },
            { key: 'Japan', name: '日本' },
            { key: 'Korea', name: '韩国' },
        ];
    }
};