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
              <span class="info-plays">▶ {{ formatPlayCount(movie) }}</span>
            </div>
            <div class="movie-meta"><span>{{ movie.region }}</span> | <span>{{ movie.genres.join(' / ') }}</span></div>
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
    };
  },
  computed: {
    /**
     * 根据所有筛选器和排序规则，计算最终显示的电影列表
     */
    rankedMovies() {
      // 1. 先进行过滤 (Filter)
      let filtered = [...this.allMovies];

      // 1.1 按“影片等级”筛选
      if (this.activeFilters.level === 'vip') {
        filtered = filtered.filter(m => m.isVip);
      }

      // 1.2 按“内容类型”筛选
      if (this.activeFilters.genre !== 'all') {
        filtered = filtered.filter(m => m.genres.includes(this.activeFilters.genre));
      }

      // 2. 再进行排序 (Sort)
      const sorted = filtered.sort((a, b) => {
        // 如果是按好评排行，则忽略时间范围，直接比较评分
        if (this.activeFilters.sortBy === 'rating') {
          return b.rating - a.rating;
        }

        // 如果是按热播排行，则根据时间范围选择不同的热度字段
        switch (this.activeFilters.timeRange) {
          case 'week':
            return b.weeklyPlayCount - a.weeklyPlayCount;
          case 'month':
            return b.monthlyPlayCount - a.monthlyPlayCount;
          case 'all':
          default:
            return b.playCount - a.playCount; // 总热度
        }
      });

      return sorted;
    },
  },
  methods: {
    /**
     * 将后端返回的单个电影对象转换为前端需要的格式
     */
    mapMovieData(movie) {
      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      const movieCountries = movie.countries_Str ? movie.countries_Str.split(',').map(c => c.trim()) : [];
      return {
        id: movie.movie_id,
        title: movie.title,
        playCount: movie.popularity || 0,
        weeklyPlayCount: movie.weekly_Popularity || 0,
        monthlyPlayCount: movie.monthly_Popularity || 0,
        rating: movie.vote_Average || 0,
        isVip: movie.access_Level > 1,
        genres: movieGenres,
        region: movieCountries.length > 0 ? movieCountries[0] : '未知',
        posterUrl: 'https://via.placeholder.com/200x300.png?text=' + encodeURIComponent(movie.title),
      };
    },

    /**
     * 从所有电影数据中动态生成类型列表
     */
    generateGenres(movies) {
      const genreSet = new Set();
      movies.forEach(movie => {
        movie.genres.forEach(g => genreSet.add(g));
      });
      this.genres = [
        { key: 'all', name: '全部类型' },
        ...Array.from(genreSet).map(g => ({ key: g, name: g }))
      ];
    },

    /**
     * 获取并处理所有排行榜数据
     */
    async fetchData() {
      this.loading = true;
      try {
        const rawMovies = await movieService.getList();
        this.allMovies = rawMovies.map(this.mapMovieData);
        this.generateGenres(this.allMovies);
      } catch (error) {
        console.error("获取排行榜数据失败:", error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * 统一的筛选器设置方法，包含交互优化
     */
    setFilter(filterName, value) {
      if (filterName === 'sortBy' && value === 'rating') {
        this.activeFilters.timeRange = 'all';
      }

      if (filterName === 'timeRange' && this.activeFilters.sortBy === 'rating') {
        this.activeFilters.sortBy = 'plays';
      }

      this.activeFilters[filterName] = value;
    },

    /**
     * 动态计算按钮的CSS class
     */
    getButtonClass(filterName, value) {
      return ['filter-btn', { 'active': this.activeFilters[filterName] === value }];
    },

    /**
     * 根据当前时间范围，格式化并显示对应的播放量
     */
    formatPlayCount(movie) {
      let count;
      switch (this.activeFilters.timeRange) {
        case 'week':
          count = movie.weeklyPlayCount;
          break;
        case 'month':
          count = movie.monthlyPlayCount;
          break;
        case 'all':
        default:
          count = movie.playCount;
          break;
      }
      if (count >= 10000) return (count / 10000).toFixed(1) + ' 万';
      return Math.round(count).toLocaleString(); // 对数值取整并格式化
    },

    /**
     * 从路由参数更新筛选器状态
     */
    updateFiltersFromRoute(route) {
      const { time, level, genre, sortBy } = route.query;
      this.activeFilters.timeRange = ['all', 'week', 'month'].includes(time) ? time : 'all';
      this.activeFilters.level = level || 'all';
      this.activeFilters.genre = genre || 'all';
      this.activeFilters.sortBy = (sortBy === 'rating' || sortBy === 'plays') ? sortBy : 'plays';
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
.ranking-page-content{padding:2rem 4rem;min-height:calc(100vh - 68px)}.ranking-header h1{font-size:2.8rem;font-weight:700;color:#fff;letter-spacing:-1px}.ranking-header p{color:#a0a0a0;margin-top:-.5rem}
.filters-bar{display:flex;flex-direction:column;gap:1.5rem;margin-block:2.5rem;padding:1.5rem 0;border-top:1px solid #303030;border-bottom:1px solid #303030}.filter-group{display:flex;align-items:center;gap:1rem;flex-wrap:wrap}.filter-label{font-weight:500;color:#a0a0a0;min-width:80px;font-size:.9rem;text-align:right;padding-right:1rem}
.filter-btn {
  background-color: #333333;
  color: #e5e5e5;
  border: 1px solid transparent;
  border-radius: 4px;
  padding: 0.6rem 1.2rem;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  transition: all 0.2s ease-in-out;
  outline: none;
}
.filter-btn:hover {
  background-color: #4d4d4d;
}
.filter-btn.active {
  background-color: #e50914;
  color: #ffffff;
  font-weight: bold;
}
.loading-container{display:flex;justify-content:center;padding-top:5rem}.loading-spinner{border:5px solid #333;border-top:5px solid #e50914;border-radius:50%;width:50px;height:50px;animation:spin 1s linear infinite}@keyframes spin{0%{transform:rotate(0)}100%{transform:rotate(360deg)}}.ranking-list{list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:1rem}.ranking-item{display:flex;align-items:center;gap:1.5rem;background-color:#1c1c1c;padding:1rem;border-radius:8px;transition:all .3s ease-in-out;border:1px solid transparent}.ranking-item:hover{background-color:#252525;transform:translateY(-5px);border-color:#444}.rank-number{font-size:2.2rem;font-weight:700;color:#666;width:50px;text-align:center;flex-shrink:0;font-style:italic}.rank-number.top-3{color:#e50914}.poster{width:80px;height:120px;object-fit:cover;border-radius:4px;flex-shrink:0}.movie-details{flex-grow:1}.movie-title{margin:0 0 .5rem;font-size:1.2rem;color:#fff;display:flex;align-items:center;gap:.5rem}.movie-info{display:flex;gap:1rem;font-size:.9rem;margin-bottom:.5rem}.info-rating{color:#f5c518;font-weight:700}.info-plays{color:#b3b3b3}.movie-meta{font-size:.8rem;color:#8c8c8c}.play-btn{background-color:#e50914;color:#fff;border:none;padding:.6rem 1.2rem;border-radius:4px;font-weight:700;cursor:pointer;transition:opacity .3s}.play-btn:hover{opacity:.8}.no-results{text-align:center;padding:4rem;color:#8c8c8c;font-size:1.2rem}.vip-badge-inline{background-color:gold;color:#141414;padding:2px 6px;border-radius:4px;font-size:.7rem;font-weight:700}.list-fade-enter-active,.list-fade-leave-active{transition:all .5s}.list-fade-enter,.list-fade-leave-to{opacity:0;transform:translateY(20px)}@media (max-width:768px){.ranking-page-content{padding:1rem 1.5rem}.filter-group{flex-direction:column;align-items:flex-start}.filter-label{text-align:left;margin-bottom:.5rem}.filter-btn{width:100%;text-align:center}}
</style>
