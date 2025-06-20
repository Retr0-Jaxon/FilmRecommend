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
          <!-- 【修改 1】: 添加 @click 事件，使整行可点击跳转 -->
          <li v-for="movie in paginatedMovies" :key="movie.id" class="result-item" @click="goToDetailPage(movie.id,movie.isVip)">
            <img :src="movie.posterUrl" :alt="movie.title" class="poster" />
            <div class="movie-details">
              <h3 class="movie-title">
                {{ movie.title }}
                <span v-if="movie.isVip" class="vip-badge-inline">VIP</span>
                
              </h3>
              <div class="movie-info">
                <!-- 【修改 2】: 将导演名字变为可点击的链接 -->
                <span>
                  导演:
                  <a @click.stop="searchByDirector(movie.director)" class="director-link">
                    {{ movie.director }}
                  </a>
                </span>
                <span>类型: {{ movie.genres.join(' / ') }}</span>
              </div>
            </div>
            <!-- 【修改 3】: 给播放按钮添加 .stop 修饰符，防止点击它时触发整行跳转 -->
            <!-- <button @click.stop="playMovie(movie)" class="play-btn">▶ 播放</button> -->
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
import { getInfo } from '@/api/login';
import axios from 'axios';
import { movieService } from '../services/movieService';

export default {
  name: 'SearchPage',
  data() {
    return {
      loading: true,
      searchQuery: '',
      allMovies: [],
      currentPage: 1,
      itemsPerPage: 10,
    };
  },
  computed: {
    filteredMovies() {
      if (!this.searchQuery.trim()) {
        return [];
      }
      const lowerCaseQuery = this.searchQuery.trim().toLowerCase();
      return this.allMovies.filter(movie => {
        const titleMatch = movie.title.toLowerCase().includes(lowerCaseQuery);
        const directorMatch = movie.director && movie.director.toLowerCase().includes(lowerCaseQuery);
        return titleMatch || directorMatch;
      });
    },
    totalPages() {
      return Math.ceil(this.filteredMovies.length / this.itemsPerPage);
    },
    paginatedMovies() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;
      return this.filteredMovies.slice(startIndex, endIndex);
    },
  },
  methods: {
    async fetchData() {
      this.loading = true;
      try {
        const rawMovies = await movieService.getList();
        this.allMoviesPromise = rawMovies.map(this.mapMovieData);
        this.allMovies = await Promise.all(this.allMoviesPromise);
      } catch (error) {
        console.error("搜索页面获取数据失败:", error);
      } finally {
        this.loading = false;
      }
    },
    async mapMovieData(movie) {
      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      var posterUrl = await movieService.getMoviePosterUrl(movie.movie_id)
      return {
        id: movie.movie_id,
        title: movie.title,
        director: movie.director || '未知',
        isVip: movie.access_Level >= 1,
        genres: movieGenres,
        // posterUrl: 'https://via.placeholder.com/200x300.png?text=' + encodeURIComponent(movie.title),
        posterUrl: posterUrl,
      };
    },
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

    // --- 【新增方法】 ---

    /**
     * 【新增】跳转到电影详情页
     * @param {number} movieId - 电影的ID
     */
     async goToDetailPage(movieId,movieIsVip) {
      // 确保你的详情页路由名为 'MovieDetail'

      if (movieIsVip){
        try {
          const response = await getInfo();
          // 假设返回数据结构：{ user: { roles: [{ roleId: 数字 }] } }
          this.isVip = response.user.roles.some(role => role.roleId === 101);
        } catch (error) {
          console.error('检查VIP状态出错:', error);
          this.isVip = false;
        }
        if (this.isVip){

          this.$router.push({ name: 'MovieDetail', params: { id: movieId } });
        }else {
          alert("你还不是VIP，请充值！");
        }
      }else {
        this.$router.push({ name: 'MovieDetail', params: { id: movieId } });
      }
    },

    /**
     * 【新增】通过点击导演姓名来更新搜索查询
     * @param {string} directorName - 导演的名字
     */
    searchByDirector(directorName) {
      if (directorName && directorName !== '未知') {
        this.searchQuery = directorName;
      }
    },

    /**
     * 【新增】处理播放按钮点击（示例）
     * @param {object} movie - 电影对象
     */
    playMovie(movie) {
      alert(`开始播放: ${movie.title}`);
      // 在这里可以添加实际的播放逻辑，例如弹出一个播放器模态框
    }
  },
  watch: {
    searchQuery() {
      this.currentPage = 1;
    }
  },
  created() {
    // 【新增】检查URL中是否有查询参数，实现从详情页点击导演返回搜索
    const initialQuery = this.$route.query.query;
    if (initialQuery) {
      this.searchQuery = initialQuery;
    }
    this.fetchData();
  }
};
</script>

<style scoped>
/* 【新增样式】 */


.result-item {
  transition: background-color 0.2s ease-in-out;
  cursor: pointer; /* 提示用户这是可点击的 */
}
.result-item:hover {
  background-color: #2a2a2a; /* 鼠标悬停时改变背景色 */
}
.director-link {
  color: #a0c4ff; /* 使用一种醒目的颜色以示可点击 */
  text-decoration: underline;
  text-decoration-color: transparent;
  transition: text-decoration-color 0.2s;
}
.director-link:hover {
  color: #e50914; /* 悬停时变为品牌色 */
  text-decoration-color: #e50914;
}

/* --- 原有样式 --- */
.pagination { display: flex; justify-content: center; align-items: center; margin-top: 2rem; padding-bottom: 2rem; }
.page-btn { background-color: #333; color: #fff; border: 1px solid #555; padding: 0.5rem 1rem; margin: 0 0.5rem; cursor: pointer; border-radius: 4px; transition: background-color 0.3s; }
.page-btn:hover:not(:disabled) { background-color: #e50914; }
.page-btn:disabled { background-color: #1a1a1a; color: #555; cursor: not-allowed; }
.page-info { color: #8c8c8c; font-weight: 500; }
.loading-container { display: flex; flex-direction: column; justify-content: center; align-items: center; margin-top: 5rem; color: #8c8c8c; }
.loading-spinner { border: 5px solid #333; border-top: 5px solid #e50914; border-radius: 50%; width: 50px; height: 50px; animation: spin 1s linear infinite; margin-bottom: 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.search-page-content { padding: 2rem 4rem; }
.search-header h1 { font-size: 2.5rem; margin-bottom: 1.5rem; color: floralwhite;}
.search-input-wrapper { position: relative; }
.search-icon { position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); width: 1.5rem; height: 1.5rem; color: #8c8c8c; }
.search-input { width: 100%; background-color: #333; border: 1px solid #555; border-radius: 4px; padding: 1rem 1rem 1rem 3.5rem; color: #fff; font-size: 1.2rem; }
.search-results-container { margin-top: 2rem; }
.initial-prompt, .no-results { text-align: center; color: #8c8c8c; font-size: 1.2rem; padding: 4rem 0; }
.search-results-list { list-style: none; padding: 0; margin: 0; }
.result-item { display: flex; align-items: center; background-color: #1f1f1f; border-radius: 4px; margin-bottom: 1rem; padding: 1rem; }
.poster { width: 80px; height: 120px; object-fit: cover; border-radius: 4px; margin-right: 1.5rem; }
.movie-details { flex-grow: 1; }
h3.movie-title { font-size: 1.4rem; margin-bottom: 0.5rem; color: floralwhite}
.movie-info { color: #8c8c8c; display: flex; flex-direction: column; gap: 0.3rem; }
.vip-badge-inline{background-color:gold;color:#141414;padding:2px 6px;border-radius:4px;font-size:.7rem;font-weight:700}
.play-btn { background-color: #e50914; color: #fff; border: none; padding: 0.6rem 1.2rem; border-radius: 4px; cursor: pointer; font-size: 1rem; font-weight: bold; margin-left: auto; }
.list-fade-enter-active, .list-fade-leave-active { transition: opacity 0.5s; }
.list-fade-enter, .list-fade-leave-to { opacity: 0; }
</style>
