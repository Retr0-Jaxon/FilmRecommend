<template>
  <div class="movie-detail-page">
    <!-- 返回按钮 -->
    <button @click="goBack" class="back-button">← 返回搜索</button>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载影片详情...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <h2>出错了</h2>
      <p>{{ error }}</p>
    </div>

    <!-- 电影详情 -->
    <div v-else-if="movie" class="detail-content">
      <img :src="posterUrl" :alt="movie.title" class="detail-poster"/>
      <div class="detail-info">
        <h1 class="detail-title">
          {{ movie.title }}
          <span v-if="movie.access_Level > 1" class="vip-badge-inline">VIP</span>
        </h1>
        <p class="tagline">{{ movie.tagline }}</p>

        <div class="info-grid">
          <div><strong>类型:</strong> {{ movie.genres_Str }}</div>
          <div><strong>国家:</strong> {{ movie.countries_Str }}</div>
          <div>
            <strong>导演:</strong>
            <!-- 点击导演名字会触发搜索 -->
            <a @click="searchByDirector(movie.director)" class="director-link">
              {{ movie.director }}
            </a>
          </div>
          <div><strong>评分:</strong> {{ movie.vote_Average }} / 10 ({{ movie.vote_Count }} 票)</div>
        </div>

        <h3 class="overview-title">剧情简介</h3>
        <p class="overview">{{ movie.overview }}</p>

        <div class="popularity-stats">
          <h4>热度指数</h4>
          <span>周热度: {{ movie.weekly_Popularity.toFixed(2) }}</span>
          <span>月热度: {{ movie.monthly_Popularity.toFixed(2) }}</span>
          <span>总热度: {{ movie.popularity.toFixed(2) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { movieService } from '../services/movieService';

export default {
  name: 'MovieDetailPage',
  props: {
    // 从路由接收 'id' 参数
    id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      movie: null,
      loading: true,
      error: null
    };
  },
  computed: {
    posterUrl() {
      if (this.movie) {
        // 使用一个更大的占位图
        return `https://via.placeholder.com/400x600.png?text=${encodeURIComponent(this.movie.title)}`;
      }
      return '';
    }
  },
  methods: {
    async fetchMovieData() {
      this.loading = true;
      this.error = null;
      try {
        this.movie = await movieService.getMovieById(this.id);
      } catch (error) {
        console.error("获取电影详情失败:", error);
        this.error = "无法找到该影片的详细信息。";
      } finally {
        this.loading = false;
      }
    },
    // 点击导演姓名，跳转到搜索页并以导演名作为查询词
    searchByDirector(directorName) {
      this.$router.push({
        name: 'Search',
        query: { query: directorName }
      });
    },
    goBack() {
      this.$router.back(); // 或者 this.$router.push({ name: 'Search' });
    }
  },
  created() {
    this.fetchMovieData();
  }
};
</script>

<style scoped>
.movie-detail-page {
  padding: 2rem 4rem;
  max-width: 1200px;
  margin: 0 auto;
}
.back-button {
  background: #333;
  color: #fff;
  border: 1px solid #555;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 2rem;
  font-size: 1rem;
}
.back-button:hover {
  background-color: #e50914;
}
.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  color: #8c8c8c;
}
.loading-spinner { border: 5px solid #333; border-top: 5px solid #e50914; border-radius: 50%; width: 50px; height: 50px; animation: spin 1s linear infinite; margin-bottom: 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.error-container h2 { color: #e50914; }

.detail-content {
  display: flex;
  gap: 2.5rem;
}
.detail-poster {
  width: 300px;
  height: auto;
  min-width: 300px;
  border-radius: 8px;
  object-fit: cover;
}
.detail-info {
  flex-grow: 1;
}
.detail-title {
  font-size: 3rem;
  margin-top: 0;
  margin-bottom: 0.5rem;
}
.tagline {
  font-style: italic;
  color: #8c8c8c;
  margin-top: 0;
  margin-bottom: 2rem;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
  color: #ccc;
}
.info-grid strong {
  color: #fff;
}
.director-link {
  color: #58a6ff; /* A typical link color */
  text-decoration: underline;
  cursor: pointer;
}
.director-link:hover {
  color: #e50914;
}
.overview-title {
  font-size: 1.5rem;
  border-left: 4px solid #e50914;
  padding-left: 0.8rem;
  margin-bottom: 1rem;
}
.overview {
  line-height: 1.6;
  color: #ccc;
  margin-bottom: 2rem;
}
.popularity-stats {
  background-color: #1f1f1f;
  padding: 1rem;
  border-radius: 4px;
}
.popularity-stats h4 {
  margin-top: 0;
}
.popularity-stats span {
  margin-right: 1.5rem;
  color: #8c8c8c;
}
.vip-badge-inline { font-size: 1rem; background-color: #e50914; color: #fff; padding: 4px 8px; border-radius: 3px; margin-left: 0.5rem; vertical-align: middle; }
</style>
