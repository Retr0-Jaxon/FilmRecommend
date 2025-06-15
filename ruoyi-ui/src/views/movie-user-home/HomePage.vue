<template>
  <div class="home-page-container">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载影片...</p>
    </div>

    <main v-else>
      <!-- 主视觉区 -->
      <section class="hero" :style="{ backgroundImage: 'url(' + heroContent.backgroundImage + ')' }">
        <button @click="goToLoginPage" class="btn btn-login">登录</button>
        <div class="hero-content">
          <h1 class="hero-title">{{ heroContent.title }}</h1>
          <p class="hero-description">{{ heroContent.description }}</p>
          <div class="hero-buttons">
            <button class="btn btn-play">▶ 播放</button>
            <button class="btn btn-list">+ 我的列表</button>
          </div>
        </div>
        <div class="hero-fade"></div>
      </section>

      <!-- 内容行 -->
      <section class="content-rows">
        <!-- 1. 热播排行 -->
        <div class="row">
          <h2 class="row-title clickable" @click="goToRankingPage({ sortBy: 'plays' })">
            热播排行 <span class="view-all-arrow">›</span>
          </h2>
          <div class="thumbnails-container">
            <div class="thumbnail" v-for="item in hotMovies" :key="item.id">
              <img :src="item.posterUrl" :alt="item.title">
              <span v-if="item.isVip" class="vip-badge">VIP</span>
            </div>
          </div>
        </div>

        <!-- 2. 按类型展示 -->
        <div v-for="genre in genres.filter(g => g.key !== 'all')" :key="genre.key" class="row">
          <h2 class="row-title clickable" @click="goToRankingPage({ genre: genre.key })">
            {{ genre.name }}精选 <span class="view-all-arrow">›</span>
          </h2>
          <div class="thumbnails-container">
            <div class="thumbnail" v-for="item in moviesByGenre[genre.key]" :key="item.id">
              <img :src="item.posterUrl" :alt="item.title">
              <span v-if="item.isVip" class="vip-badge">VIP</span>
            </div>
          </div>
        </div>

        <!-- 3. 按地区展示 -->
        <div v-for="region in regions.filter(r => r.key !== 'all')" :key="region.key" class="row">
          <h2 class="row-title">{{ region.name }}电影</h2>
          <div class="thumbnails-container">
            <div class="thumbnail" v-for="item in moviesByRegion[region.key]" :key="item.id">
              <img :src="item.posterUrl" :alt="item.title">
              <span v-if="item.isVip" class="vip-badge">VIP</span>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { movieService } from '../services/movieService';

export default {
  name: 'HomePage',
  data() {
    return {
      loading: true,
      heroContent: {
        title: '纸房子',
        description: '八名盗贼将自己与人质反锁在西班牙皇家造币厂内，他们背后的犯罪首脑则试图操纵警察实现自己的计划…',
        backgroundImage: 'https://image.tmdb.org/t/p/original/pA4sNvco71n729r5n5Y1xGDHnS6.jpg',
      },
      allMovies: [],
      genres: [],
      regions: [],
    };
  },
  computed: {
    hotMovies() {
      return [...this.allMovies].sort((a, b) => b.playCount - a.playCount).slice(0, 10);
    },
    moviesByGenre() {
      const grouped = {};
      this.genres.forEach(genre => {
        if (genre.key !== 'all') {
          grouped[genre.key] = this.allMovies
              .filter(movie => movie.genre === genre.key)
              .sort((a, b) => b.rating - a.rating)
              .slice(0, 10);
        }
      });
      return grouped;
    },
    moviesByRegion() {
      const grouped = {};
      this.regions.forEach(region => {
        if (region.key !== 'all') {
          grouped[region.key] = this.allMovies
              .filter(movie => movie.region === region.key)
              .sort((a,b) => new Date(b.releaseDate) - new Date(a.releaseDate))
              .slice(0, 10);
        }
      });
      return grouped;
    }
  },
  methods: {
    /**
     * 跳转到排行榜页面的方法 (已更新)
     * @param {object} params - 包含预设筛选条件的参数对象
     */
    goToRankingPage(params) {
      // RankingPage 的路由 query 参数名是: time, level, genre, sortBy
      // 我们将 HomePage 的点击意图映射到这些参数上
      const queryParams = {
        time: params.timeRange || 'all',
        level: params.level || 'all',
        genre: params.genre || 'all',
        sortBy: params.sortBy || 'plays', // 默认按热播排行
      };

      this.$router.push({
        name: 'Ranking',
        query: queryParams
      });
    },
    async fetchData() {
      this.loading = true;
      try {
        const [movies, genres, regions] = await Promise.all([
          movieService.getAllMovies(),
          movieService.getGenres(),
          movieService.getRegions()
        ]);

        this.allMovies = movies;
        this.genres = genres;
        this.regions = regions;
      } catch (error) {
        console.error("获取首页数据失败:", error);
      } finally {
        this.loading = false;
      }
    }
  },
  created() {
    this.fetchData();
  },
};
</script>

<style scoped>
/* 样式与之前版本保持一致 */
.loading-container{display:flex;flex-direction:column;justify-content:center;align-items:center;height:calc(100vh - 68px);color:#8c8c8c}.loading-spinner{border:5px solid #333;border-top:5px solid #e50914;border-radius:50%;width:50px;height:50px;animation:spin 1s linear infinite;margin-bottom:1rem}@keyframes spin{0%{transform:rotate(0)}100%{transform:rotate(360deg)}}.hero{position:relative;height:calc(100vh - 68px);min-height:500px;background-size:cover;background-position:center top;display:flex;align-items:center;padding:0 3.5rem}.hero-content{position:relative;z-index:5;max-width:40%}.hero-title{font-size:4rem;font-weight:700;margin-bottom:1.5rem;text-shadow:2px 2px 4px rgba(0,0,0,.45)}.hero-description{font-size:1.2rem;line-height:1.5;margin-bottom:2rem;max-width:500px;text-shadow:1px 1px 2px rgba(0,0,0,.7)}.hero-fade{position:absolute;bottom:0;left:0;width:100%;height:10rem;background-image:linear-gradient(to top,#141414,transparent)}.hero-buttons{display:flex;gap:1rem}.btn{padding:.7rem 1.8rem;border:none;border-radius:4px;font-size:1.1rem;font-weight:700;cursor:pointer;display:flex;align-items:center;gap:.7rem;transition:opacity .3s}.btn:hover{opacity:.8}.btn-play{background:#fff;color:#000}.btn-list{background:rgba(109,109,110,.7);color:#fff}.content-rows{padding:0 3.5rem;margin-top:-8vh;position:relative;z-index:10}.row{margin-bottom:3rem}.row-title{font-size:1.4rem;font-weight:700;margin-bottom:1rem}.thumbnails-container{display:flex;overflow-x:auto;overflow-y:hidden;padding-bottom:1rem;-ms-overflow-style:none;scrollbar-width:none}.thumbnails-container::-webkit-scrollbar{display:none}.thumbnail{position:relative;flex:0 0 16.66%;padding:0 .2rem;transition:transform .3s ease;cursor:pointer}.thumbnail img{width:100%;border-radius:4px;display:block}.thumbnail:hover{transform:scale(1.15);z-index:20}.vip-badge{position:absolute;top:8px;left:8px;background-color:#e50914;color:#fff;padding:4px 8px;border-radius:4px;font-size:.8rem;font-weight:700;z-index:2;box-shadow:0 0 5px rgba(0,0,0,.5)}.row-title.clickable{cursor:pointer;display:inline-flex;align-items:center;transition:color .3s}.row-title.clickable:hover{color:#e50914}.view-all-arrow{font-size:1.8rem;margin-left:.5rem;font-weight:400;transition:transform .3s}.row-title.clickable:hover .view-all-arrow{transform:translateX(5px)}
</style>