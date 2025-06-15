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
          placeholder="输入电影名称或导演..."
          autofocus
        />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载影片数据库...</p>
    </div>

    <!-- 搜索结果区域 -->
    <div v-else class="search-results-container">
      <!-- 初始状态 -->
      <div v-if="!searchQuery.trim()" class="initial-prompt">
        <p>开始输入以搜索影片。</p>
      </div>

      <!-- 搜索后 -->
      <div v-else>
        <!-- 搜索结果列表 (迭代 paginatedMovies) -->
        <transition-group v-if="paginatedMovies.length > 0" name="list-fade" tag="ul" class="search-results-list">
          <li v-for="movie in paginatedMovies" :key="movie.id" class="result-item">
            <img :src="movie.posterUrl" :alt="movie.title" class="poster" />
            <div class="movie-details">
              <h3 class="movie-title">
                {{ movie.title }}
                <span v-if="movie.isVip" class="vip-badge-inline">VIP</span>
              </h3>
              <div class="movie-info">
                <span>导演: {{ movie.director }}</span>
                <span>类型: {{ movie.genres.join(' / ') }}</span>
              </div>
            </div>
            <button class="play-btn">▶ 播放</button>
          </li>
        </transition-group>

        <!-- 无搜索结果 -->
        <div v-else class="no-results">
          <p>未找到与 “<strong>{{ searchQuery }}</strong>” 相关的影片。</p>
        </div>

        <!-- 分页控制器 -->
        <div v-if="totalPages > 1" class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">上一页</button>
          <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { movieService } from '../services/movieService';

export default {
  name: 'SearchPage',
  data() {
    return {
      loading: true,
      searchQuery: '',      // 绑定到输入框的值
      allMovies: [],        // 存储从API获取的所有电影（映射后）
      currentPage: 1,       // 当前页码
      itemsPerPage: 10,     // 每页显示的项目数
    };
  },
  computed: {
    /**
     * [第一步] 计算属性：根据 searchQuery 实时筛选所有匹配的电影
     * 返回的是所有匹配结果，不进行分页。
     */
    filteredMovies() {
      if (!this.searchQuery.trim()) {
        return [];
      }
      const lowerCaseQuery = this.searchQuery.trim().toLowerCase();
      return this.allMovies.filter(movie => {
        const titleMatch = movie.title.toLowerCase().includes(lowerCaseQuery);
        // [新功能] 增加对导演的搜索，并确保 director 字段存在
        const directorMatch = movie.director && movie.director.toLowerCase().includes(lowerCaseQuery);
        return titleMatch || directorMatch;
      });
    },

    /**
     * [第二步] 计算属性：计算总页数
     */
    totalPages() {
      return Math.ceil(this.filteredMovies.length / this.itemsPerPage);
    },

    /**
     * [第三步] 计算属性：对已筛选的结果进行分页
     * 这个属性最终用于在模板中渲染列表。
     */
    paginatedMovies() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;
      return this.filteredMovies.slice(startIndex, endIndex);
    },
  },
  methods: {
    /**
     * [新增] 从后端获取所有电影数据
     */
    async fetchData() {
      this.loading = true;
      try {
        const rawMovies = await movieService.getList();
        this.allMovies = rawMovies.map(this.mapMovieData);
      } catch (error) {
        console.error("搜索页面获取数据失败:", error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * [新增] 数据映射函数，确保包含 director 字段
     */
    mapMovieData(movie) {
      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      return {
        id: movie.movie_id,
        title: movie.title,
        director: movie.director || '未知', // [关键] 映射导演字段
        isVip: movie.access_Level > 1,
        genres: movieGenres,
        posterUrl: 'https://via.placeholder.com/200x300.png?text=' + encodeURIComponent(movie.title),
      };
    },

    // --- 分页方法 ---
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
  },
  watch: {
    /**
     * [关键] 监听搜索词的变化，当用户重新输入时，自动重置到第一页
     */
    searchQuery() {
      this.currentPage = 1;
    }
  },
  created() {
    this.fetchData();
  }
};
</script>

<style scoped>
/* 添加分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  padding-bottom: 2rem;
}
.page-btn {
  background-color: #333;
  color: #fff;
  border: 1px solid #555;
  padding: 0.5rem 1rem;
  margin: 0 0.5rem;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}
.page-btn:hover:not(:disabled) {
  background-color: #e50914;
}
.page-btn:disabled {
  background-color: #1a1a1a;
  color: #555;
  cursor: not-allowed;
}
.page-info {
  color: #8c8c8c;
  font-weight: 500;
}

/* 沿用或添加其他页面的通用样式 */
.loading-container { display: flex; flex-direction: column; justify-content: center; align-items: center; margin-top: 5rem; color: #8c8c8c; }
.loading-spinner { border: 5px solid #333; border-top: 5px solid #e50914; border-radius: 50%; width: 50px; height: 50px; animation: spin 1s linear infinite; margin-bottom: 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.search-page-content { padding: 2rem 4rem; }
.search-header h1 { font-size: 2.5rem; margin-bottom: 1.5rem; }
.search-input-wrapper { position: relative; }
.search-icon { position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); width: 1.5rem; height: 1.5rem; color: #8c8c8c; }
.search-input { width: 100%; background-color: #333; border: 1px solid #555; border-radius: 4px; padding: 1rem 1rem 1rem 3.5rem; color: #fff; font-size: 1.2rem; }
.search-results-container { margin-top: 2rem; }
.initial-prompt, .no-results { text-align: center; color: #8c8c8c; font-size: 1.2rem; padding: 4rem 0; }
.search-results-list { list-style: none; padding: 0; margin: 0; }
.result-item { display: flex; align-items: center; background-color: #1f1f1f; border-radius: 4px; margin-bottom: 1rem; padding: 1rem; }
.poster { width: 80px; height: 120px; object-fit: cover; border-radius: 4px; margin-right: 1.5rem; }
.movie-details { flex-grow: 1; }
.movie-title { font-size: 1.4rem; margin-bottom: 0.5rem; }
.movie-info { color: #8c8c8c; display: flex; flex-direction: column; gap: 0.3rem; }
.vip-badge-inline { font-size: 0.8rem; background-color: #e50914; color: #fff; padding: 2px 6px; border-radius: 3px; margin-left: 0.5rem; vertical-align: middle; }
.play-btn { background-color: #e50914; color: #fff; border: none; padding: 0.6rem 1.2rem; border-radius: 4px; cursor: pointer; font-size: 1rem; font-weight: bold; margin-left: auto; }
.list-fade-enter-active, .list-fade-leave-active { transition: opacity 0.5s; }
.list-fade-enter, .list-fade-leave-to { opacity: 0; }
</style>
