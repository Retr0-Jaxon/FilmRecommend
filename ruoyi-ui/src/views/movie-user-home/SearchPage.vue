<!-- src/components/SearchPage.vue -->
<template>
  <div class="search-page-content">
    <div class="search-header">
      <h1>搜索影片、剧集</h1>
      <div class="search-input-wrapper">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z"></path></svg>
        <input
            type="text"
            class="search-input"
            v-model="searchQuery"
            placeholder="输入电影名称..."
            autofocus
        />
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div class="search-results-container">
      <!-- 初始状态 -->
      <div v-if="!searchQuery" class="initial-prompt">
        <p>开始输入以搜索影片。</p>
      </div>

      <!-- 有搜索结果 -->
      <transition-group v-else-if="filteredMovies.length > 0" name="list-fade" tag="ul" class="search-results-list">
        <li v-for="movie in filteredMovies" :key="movie.id" class="result-item">
          <img :src="movie.posterUrl" :alt="movie.title" class="poster" />
          <div class="movie-details">
            <h3 class="movie-title">
              {{ movie.title }}
              <span v-if="movie.isVip" class="vip-badge-inline">VIP</span>
            </h3>
            <div class="movie-info">
              <span>类型: {{ movie.genre.charAt(0).toUpperCase() + movie.genre.slice(1) }}</span>
              <span>上映日期: {{ movie.releaseDate }}</span>
            </div>
          </div>
          <button class="play-btn">▶ 播放</button>
        </li>
      </transition-group>

      <!-- 无搜索结果 -->
      <div v-else class="no-results">
        <p>未找到与 “<strong>{{ searchQuery }}</strong>” 相关的影片。</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchPage',
  data() {
    return {
      searchQuery: '', // 绑定到输入框的值
      // 在真实应用中，这个数据会通过API获取
      allMovies: [
        { id: 1, title: 'Dune: Part Two', posterUrl: 'https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05ESeaB.jpg', isVip: true, releaseDate: '2024-02-27', playCount: 8500000, genre: 'scifi' },
        { id: 2, title: 'Oppenheimer', posterUrl: 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', isVip: true, releaseDate: '2023-07-19', playCount: 12000000, genre: 'suspense' },
        { id: 3, title: 'Your Name.', posterUrl: 'https://image.tmdb.org/t/p/w500/xq1Ugd62d23K2knRUx6xxuALTZB.jpg', isVip: false, releaseDate: '2016-08-26', playCount: 25000000, genre: 'romance' },
        { id: 4, title: 'The Dark Knight', posterUrl: 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', isVip: true, releaseDate: '2008-07-16', playCount: 35000000, genre: 'action' },
        { id: 5, title: 'Inception', posterUrl: 'https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq27gApcjBsovLgaq.jpg', isVip: false, releaseDate: '2010-07-15', playCount: 32000000, genre: 'scifi' },
        { id: 6, title: 'La La Land', posterUrl: 'https://image.tmdb.org/t/p/w500/uDO8zWDhfWwoFdEJhQcunderlineAXv.jpg', isVip: false, releaseDate: '2016-12-09', playCount: 18000000, genre: 'romance' },
        { id: 7, title: 'The Silence of the Lambs', posterUrl: 'https://image.tmdb.org/t/p/w500/uS9m8fHn31aR6fn6Q7nd2vRwsA5.jpg', isVip: true, releaseDate: '1991-02-01', playCount: 28000000, genre: 'suspense' },
        { id: 8, 'title': 'John Wick: Chapter 4', posterUrl: 'https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg', isVip: true, releaseDate: '2023-03-22', playCount: 15000000, genre: 'action'},
        { id: 9, 'title': 'Interstellar', posterUrl: 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', isVip: false, releaseDate: '2014-11-05', playCount: 41000000, genre: 'scifi'},
        { id: 10, 'title': 'The Notebook', posterUrl: 'https://image.tmdb.org/t/p/w500/r5va04UNo2nL35gQj4pQ22d25s8.jpg', isVip: false, releaseDate: '2004-06-25', playCount: 22000000, genre: 'romance'},
        { id: 11, 'title': 'Shutter Island', posterUrl: 'https://image.tmdb.org/t/p/w500/4GDy0tA3m5Wk2a443A4sA4z424i.jpg', isVip: true, releaseDate: '2010-02-14', playCount: 30000000, genre: 'suspense'},
      ],
    };
  },
  computed: {
    /**
     * 核心计算属性：根据 searchQuery 实时筛选电影
     */
    filteredMovies() {
      // 如果搜索框为空，不返回任何结果
      if (!this.searchQuery.trim()) {
        return [];
      }

      // 将搜索词转为小写，以进行不区分大小写的匹配
      const lowerCaseQuery = this.searchQuery.trim().toLowerCase();

      return this.allMovies.filter(movie =>
          movie.title.toLowerCase().includes(lowerCaseQuery)
      );
    },
  },
};
</script>

<style scoped>
.search-page-content {
  padding: 2rem 4rem;
  min-height: calc(100vh - 68px); /* 减去导航栏高度 */
}

.search-header {
  margin-bottom: 2.5rem;
}

.search-header h1 {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
  color: #fff;
}

.search-input-wrapper {
  position: relative;
}

.search-icon {
  position: absolute;
  top: 50%;
  left: 15px;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  color: #8c8c8c;
}

.search-input {
  width: 100%;
  padding: 1rem 1rem 1rem 3rem; /* 左侧padding为图标留出空间 */
  font-size: 1.2rem;
  background-color: #333;
  color: #fff;
  border: 1px solid #555;
  border-radius: 4px;
  box-sizing: border-box;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #e50914;
  box-shadow: 0 0 0 2px rgba(229, 9, 20, 0.5);
}

.search-results-container {
  color: #8c8c8c;
}

.initial-prompt, .no-results {
  text-align: center;
  padding: 4rem 0;
  font-size: 1.2rem;
}

/* 结果列表样式 (复用 RankingPage 的样式以保持一致) */
.search-results-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  background-color: #222;
  padding: 1rem;
  border-radius: 8px;
  transition: background-color 0.3s, transform 0.3s;
}

.result-item:hover {
  background-color: #303030;
  transform: translateY(-5px);
}

.poster {
  width: 60px;
  height: 90px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.movie-details{flex-grow:1}.movie-title{margin:0 0 .5rem;font-size:1.2rem;color:#fff;display:flex;align-items:center}.movie-info{display:flex;gap:1rem;font-size:.9rem;color:#8c8c8c}.movie-info span{background:#333;padding:2px 8px;border-radius:10px}.play-btn{background-color:#e50914;color:#fff;border:none;padding:.6rem 1.2rem;border-radius:4px;font-weight:700;cursor:pointer;transition:opacity .3s}.play-btn:hover{opacity:.8}
.vip-badge-inline {
  display: inline-block;
  margin-left: 0.5rem;
  background-color: gold;
  color: #141414;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: bold;
}
.list-fade-enter-active,.list-fade-leave-active{transition:all .5s}.list-fade-enter,.list-fade-leave-to{opacity:0;transform:translateY(20px)}
</style>