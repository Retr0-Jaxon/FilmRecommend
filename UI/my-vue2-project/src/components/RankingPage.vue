<template>
  <div class="ranking-page-content">
    <div class="ranking-header">
      <h1>电影排行榜</h1>
      <p>根据您选择的维度实时更新</p>
    </div>

    <!-- 筛选和排序控制栏 -->
    <div class="filters-bar">
      <!-- 维度一：时间范围 -->
      <div class="filter-group">
        <span class="filter-label">时间范围:</span>
        <button
            @click="setFilter('timeRange', 'all')"
            :class="getButtonClass('timeRange', 'all')"
        >全部排行</button>
        <button
            @click="setFilter('timeRange', 'month')"
            :class="getButtonClass('timeRange', 'month')"
        >本月排行</button>
        <button
            @click="setFilter('timeRange', 'week')"
            :class="getButtonClass('timeRange', 'week')"
        >本周排行</button>
      </div>

      <!-- 维度二：影片等级 -->
      <div class="filter-group">
        <span class="filter-label">影片等级:</span>
        <button @click="setFilter('level', 'all')" :class="getButtonClass('level', 'all')">全部影片</button>
        <button @click="setFilter('level', 'vip')" :class="getButtonClass('level', 'vip')">VIP 专享</button>
      </div>

      <!-- 维度三：内容类型 -->
      <div class="filter-group">
        <span class="filter-label">内容类型:</span>
        <button
            v-for="genre in genres"
            :key="genre.key"
            @click="setFilter('genre', genre.key)"
            :class="getButtonClass('genre', genre.key)"
        >
          {{ genre.name }}
        </button>
      </div>

      <!-- 维度四：排序依据 -->
      <div class="filter-group">
        <span class="filter-label">排序依据:</span>
        <button @click="setFilter('sortBy', 'plays')" :class="getButtonClass('sortBy', 'plays')">按热播排行</button>
        <button @click="setFilter('sortBy', 'rating')" :class="getButtonClass('sortBy', 'rating')">按好评排行</button>
        <button @click="setFilter('sortBy', 'latest')" :class="getButtonClass('sortBy', 'latest')">按最新发布</button>
      </div>
    </div>

    <!-- 加载与列表展示区 -->
    <div v-if="loading" class="loading-container"><div class="loading-spinner"></div></div>
    <div v-else class="ranking-list-container">
      <transition-group name="list-fade" tag="ol" class="ranking-list">
        <li v-for="(movie, index) in rankedMovies" :key="movie.id" class="ranking-item">
          <span :class="['rank-number', { 'top-3': index < 3 }]">{{ index + 1 }}</span>
          <img :src="movie.posterUrl" :alt="movie.title" class="poster" />
          <div class="movie-details">
            <h3 class="movie-title">{{ movie.title }} <span v-if="movie.isVip" class="vip-badge-inline">VIP</span></h3>
            <div class="movie-info">
              <span class="info-rating">★ {{ movie.rating.toFixed(1) }}</span>
              <span class="info-plays">▶ {{ formatPlayCount(movie.playCount) }}</span>
            </div>
            <div class="movie-meta"><span>{{ movie.releaseDate }}</span> | <span>{{ movie.region }}</span> | <span>{{ getGenreName(movie.genre) }}</span></div>
          </div>
          <button class="play-btn">播放</button>
        </li>
      </transition-group>
      <div v-if="rankedMovies.length === 0" class="no-results"><p>暂无符合条件的影片</p></div>
    </div>
  </div>
</template>

<script>
import { movieService } from '../services/movieService';

export default {
  name: 'RankingPage',
  data() {
    return {
      loading: true,
      allMovies: [],
      genres: [],
      activeFilters: {
        timeRange: 'all',
        level: 'all',
        genre: 'all',
        sortBy: 'plays',
      },
      // 新增：用于追踪按钮点击的瞬时状态
      clickedButton: {
        name: '',
        value: ''
      }
    };
  },
  computed: {
    rankedMovies() {
      // ... 计算属性逻辑保持不变 ...
      let movies = [...this.allMovies];
      const now = new Date();
      if (this.activeFilters.timeRange === 'week') {
        const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        movies = movies.filter(m => new Date(m.releaseDate) >= oneWeekAgo);
      } else if (this.activeFilters.timeRange === 'month') {
        const oneMonthAgo = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
        movies = movies.filter(m => new Date(m.releaseDate) >= oneMonthAgo);
      }
      if (this.activeFilters.level === 'vip') {
        movies = movies.filter(m => m.isVip);
      }
      if (this.activeFilters.genre !== 'all') {
        movies = movies.filter(m => m.genre === this.activeFilters.genre);
      }
      switch (this.activeFilters.sortBy) {
        case 'rating':
          movies.sort((a, b) => b.rating - a.rating);
          break;
        case 'latest':
          movies.sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate));
          break;
        default:
          movies.sort((a, b) => b.playCount - a.playCount);
          break;
      }
      return movies;
    },
  },
  methods: {
    // 统一的筛选器设置方法 (已优化)
    setFilter(filterName, value) {
      // 1. 设置筛选器状态
      this.activeFilters[filterName] = value;

      // 2. 记录哪个按钮被点击了
      this.clickedButton = { name: filterName, value };

      // 3. 短暂的视觉反馈后清除点击状态
      setTimeout(() => {
        this.clickedButton = { name: '', value: '' };
      }, 200); // 200毫秒的视觉暂留
    },

    // 新增：用于动态计算按钮class的方法
    getButtonClass(filterName, value) {
      return [
        'filter-btn',
        {
          // 主要的激活状态
          'active': this.activeFilters[filterName] === value,
          // 短暂的点击反馈状态
          'clicked': this.clickedButton.name === filterName && this.clickedButton.value === value
        }
      ];
    },

    formatPlayCount(count) {
      if (count >= 10000) return (count / 10000).toFixed(1) + ' 万';
      return count.toLocaleString();
    },
    getGenreName(genreKey) {
      const genre = this.genres.find(g => g.key === genreKey);
      return genre ? genre.name : '未知';
    },
    async fetchData() {
      this.loading = true;
      try {
        [this.allMovies, this.genres] = await Promise.all([
          movieService.getAllMovies(),
          movieService.getGenres(),
        ]);
      } catch (error) {
        console.error("获取排行榜数据失败:", error);
      } finally {
        this.loading = false;
      }
    },
    updateFiltersFromRoute(route) {
      const { time, level, genre, sort } = route.query;
      this.activeFilters.timeRange = time || 'all';
      this.activeFilters.level = level || 'all';
      this.activeFilters.genre = genre || 'all';
      this.activeFilters.sortBy = sort || 'plays';
    }
  },
  created() {
    this.updateFiltersFromRoute(this.$route);
    this.fetchData();
  },
  watch: {
    '$route'(to) {
      this.updateFiltersFromRoute(to);
    }
  }
};
</script>

<style scoped>
/* 根容器和头部样式保持不变 */
.ranking-page-content{padding:2rem 4rem;min-height:calc(100vh - 68px)}.ranking-header h1{font-size:2.8rem;font-weight:700;color:#fff;letter-spacing:-1px}.ranking-header p{color:#a0a0a0;margin-top:-.5rem}

/* 筛选栏样式 */
.filters-bar{display:flex;flex-direction:column;gap:1.5rem;margin-block:2.5rem;padding:1.5rem 0;border-top:1px solid #303030;border-bottom:1px solid #303030}.filter-group{display:flex;align-items:center;gap:1rem;flex-wrap:wrap}.filter-label{font-weight:500;color:#a0a0a0;min-width:80px;font-size:.9rem;text-align:right;padding-right:1rem}

/* --- 按钮样式 - 最终优化版 --- */
.filter-btn {
  background-color: #333333;
  color: #e5e5e5;
  border: 1px solid transparent; /* 默认边框透明 */
  border-radius: 4px;
  padding: 0.6rem 1.2rem;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  transition: all 0.2s ease-in-out;
  outline: none; /* 移除点击时的蓝色轮廓 */
}

.filter-btn:hover {
  background-color: #4d4d4d;
}

/* 激活状态 */
.filter-btn.active {
  background-color: #e50914;
  color: #ffffff;
  font-weight: bold;
}

/* 新增：点击瞬时反馈的样式 */
.filter-btn.clicked {
  transform: scale(0.95); /* 点击时按钮稍微缩小 */
  opacity: 0.8; /* 透明度变化 */
}

/* 列表和加载动画等其余样式保持不变 */
.loading-container{display:flex;justify-content:center;padding-top:5rem}.loading-spinner{border:5px solid #333;border-top:5px solid #e50914;border-radius:50%;width:50px;height:50px;animation:spin 1s linear infinite}@keyframes spin{0%{transform:rotate(0)}100%{transform:rotate(360deg)}}.ranking-list{list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:1rem}.ranking-item{display:flex;align-items:center;gap:1.5rem;background-color:#1c1c1c;padding:1rem;border-radius:8px;transition:all .3s ease-in-out;border:1px solid transparent}.ranking-item:hover{background-color:#252525;transform:translateY(-5px);border-color:#444}.rank-number{font-size:2.2rem;font-weight:700;color:#666;width:50px;text-align:center;flex-shrink:0;font-style:italic}.rank-number.top-3{color:#e50914}.poster{width:80px;height:120px;object-fit:cover;border-radius:4px;flex-shrink:0}.movie-details{flex-grow:1}.movie-title{margin:0 0 .5rem;font-size:1.2rem;color:#fff;display:flex;align-items:center;gap:.5rem}.movie-info{display:flex;gap:1rem;font-size:.9rem;margin-bottom:.5rem}.info-rating{color:#f5c518;font-weight:700}.info-plays{color:#b3b3b3}.movie-meta{font-size:.8rem;color:#8c8c8c}.play-btn{background-color:#e50914;color:#fff;border:none;padding:.6rem 1.2rem;border-radius:4px;font-weight:700;cursor:pointer;transition:opacity .3s}.play-btn:hover{opacity:.8}.no-results{text-align:center;padding:4rem;color:#8c8c8c;font-size:1.2rem}.vip-badge-inline{background-color:gold;color:#141414;padding:2px 6px;border-radius:4px;font-size:.7rem;font-weight:700}.list-fade-enter-active,.list-fade-leave-active{transition:all .5s}.list-fade-enter,.list-fade-leave-to{opacity:0;transform:translateY(20px)}@media (max-width:768px){.ranking-page-content{padding:1rem 1.5rem}.filter-group{flex-direction:column;align-items:flex-start}.filter-label{text-align:left;margin-bottom:.5rem}.filter-btn{width:100%;text-align:center}}
</style>