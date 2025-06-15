<template>
  <div class="ranking-page-content">
    <div class="ranking-header">
      <h1>电影排行榜</h1>
      <p>根据您选择的维度实时更新</p>
    </div>

    <!-- 筛选和排序控制栏 -->
    <div class="filters-bar">
      <!-- 维度一：影片等级 (保留) -->
      <div class="filter-group">
        <span class="filter-label">影片等级:</span>
        <button @click="setFilter('level', 'all')" :class="getButtonClass('level', 'all')">全部影片</button>
        <button @click="setFilter('level', 'vip')" :class="getButtonClass('level', 'vip')">VIP 专享</button>
      </div>

      <!-- 维度二：内容类型 (保留) -->
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

      <!-- 维度三：排序依据 (修改) -->
      <div class="filter-group">
        <span class="filter-label">排序依据:</span>
        <button @click="setFilter('sortBy', 'plays')" :class="getButtonClass('sortBy', 'plays')">按热播排行</button>
        <button @click="setFilter('sortBy', 'rating')" :class="getButtonClass('sortBy', 'rating')">按好评排行</button>
        <!-- [移除] 按最新发布按钮 -->
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
            <!-- [修改] 移除 releaseDate，只显示地区和类型 -->
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
// [重要] 假设 movieService.getList() 是获取所有电影的函数
import { movieService } from '../services/movieService';

export default {
  name: 'RankingPage',
  data() {
    return {
      loading: true,
      allMovies: [], // 存储映射后的所有电影数据
      genres: [],    // 动态生成的类型列表
      activeFilters: {
        // [移除] timeRange
        level: 'all',
        genre: 'all',
        sortBy: 'plays', // 'latest' 已不可用
      },
    };
  },
  computed: {
    /**
     * [重构] 根据当前激活的筛选器和排序规则，计算最终显示的电影列表
     */
    rankedMovies() {
      // 1. 从所有电影的副本开始
      let movies = [...this.allMovies];

      // 2. 按“影片等级”筛选
      if (this.activeFilters.level === 'vip') {
        movies = movies.filter(m => m.isVip);
      }

      // 3. 按“内容类型”筛选
      // [修正BUG] movie.genres 是数组，需要用 .includes() 判断
      if (this.activeFilters.genre !== 'all') {
        movies = movies.filter(m => m.genres.includes(this.activeFilters.genre));
      }

      // 4. 按“排序依据”排序
      switch (this.activeFilters.sortBy) {
        case 'rating':
          movies.sort((a, b) => b.rating - a.rating); // 按好评
          break;
        case 'plays':
        default:
          movies.sort((a, b) => b.playCount - a.playCount); // 按热播
          break;
        // [移除] 'latest' 排序
      }
      return movies;
    },
  },
  methods: {
    /**
     * [复用] 将后端返回的单个电影对象转换为前端需要的格式
     */
    mapMovieData(movie) {
      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      const movieCountries = movie.countries_Str ? movie.countries_Str.split(',').map(c => c.trim()) : [];
      return {
        id: movie.movie_id,
        title: movie.title,
        playCount: movie.weekly_Popularity || movie.popularity || 0,
        rating: movie.vote_Average || 0,
        isVip: movie.access_Level > 1, // 假设 >1 是 VIP
        genres: movieGenres,
        region: movieCountries.length > 0 ? movieCountries[0] : '未知',
        posterUrl: 'https://via.placeholder.com/200x300.png?text=' + encodeURIComponent(movie.title),
        releaseDate: '1970-01-01', // 填充一个值，但我们不使用它
      };
    },

    /**
     * [新增] 从所有电影数据中动态生成类型列表
     */
    generateGenres(movies) {
      const genreSet = new Set();
      movies.forEach(movie => {
        movie.genres.forEach(g => genreSet.add(g));
      });
      // 将 Set 转换为组件需要的 [{ key, name }] 格式
      this.genres = [
        { key: 'all', name: '全部类型' },
        ...Array.from(genreSet).map(g => ({ key: g, name: g }))
      ];
    },

    /**
     * [重构] 获取并处理所有排行榜数据
     */
    async fetchData() {
      this.loading = true;
      try {
        // 1. 只调用一个接口获取所有电影
        const rawMovies = await movieService.getList();

        // 2. 映射所有电影数据
        this.allMovies = rawMovies.map(this.mapMovieData);

        // 3. 动态生成类型列表
        this.generateGenres(this.allMovies);

      } catch (error) {
        console.error("获取排行榜数据失败:", error);
      } finally {
        this.loading = false;
      }
    },

    // --- 以下方法保持不变或稍作调整 ---

    setFilter(filterName, value) {
      this.activeFilters[filterName] = value;
      // [可选] 我们可以简化一下，不再需要视觉反馈的逻辑
    },

    getButtonClass(filterName, value) {
      return ['filter-btn', { 'active': this.activeFilters[filterName] === value }];
    },

    formatPlayCount(count) {
      if (count >= 10000) return (count / 10000).toFixed(1) + ' 万';
      return count.toLocaleString();
    },

    updateFiltersFromRoute(route) {
      // [调整] 移除 time 和 latest 的处理
      const { level, genre, sortBy } = route.query;
      this.activeFilters.level = level || 'all';
      this.activeFilters.genre = genre || 'all';
      // 确保 sortBy 的值是有效的
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
