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
          <h2 class="row-title clickable" @click="goToRankingPage({ region: region.key })">
            {{ region.name }}电影 <span class="view-all-arrow">›</span>
          </h2>
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

// [重要] 请将此基础URL替换为你的实际图片服务器地址
const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w500';

export default {
  name: 'HomePage',
  data() {
    return {
      loading: true,
      heroContent: {
        // Hero 内容可以保持硬编码，或在 fetchData 成功后从 hotMovies[0] 动态获取
        title: '纸房子',
        description: '八名盗贼将自己与人质反锁在西班牙皇家造币厂内，他们背后的犯罪首脑则试图操纵警察实现自己的计划…',
        backgroundImage: 'https://image.tmdb.org/t/p/original/pA4sNvco71n729r5n5Y1xGDHnS6.jpg',
      },
      allMovies: [], // 存储从后端获取的、按热度排序的所有电影
      genres: [],    // 动态生成的类型列表
      regions: [],   // 动态生成的地区列表
    };
  },
  computed: {
    /**
     * 热播电影列表 (取 allMovies 的前10个)
     */
    hotMovies() {
      return this.allMovies.slice(0, 10);
    },

    /**
     * 按类型分组的电影列表，按评分排序
     */
    moviesByGenre() {
      const grouped = {};
      this.genres.forEach(genre => {
        if (genre.key !== 'all') {
          grouped[genre.key] = this.allMovies
            .filter(movie => movie.genres.includes(genre.key))
            .sort((a, b) => b.rating - a.rating) // 按评分重新排序
            .slice(0, 10);
        }
      });
      return grouped;
    },

    /**
     * 按地区分组的电影列表，按发布日期排序
     */
    moviesByRegion() {
      const grouped = {};
      this.regions.forEach(region => {
        if (region.key !== 'all') {
          grouped[region.key] = this.allMovies
            .filter(movie => movie.region === region.key)
            .sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate)) // 按日期重新排序
            .slice(0, 10);
        }
      });
      return grouped;
    }
  },
  methods: {
    goToLoginPage(){
      // 假设登录页路由名为 Login
      this.$router.push({ name: 'Login' });
    },
    /**
     * 跳转到排行榜页面
     */
    goToRankingPage(params) {
      const queryParams = {
        time: params.timeRange || 'all',
        level: params.level || 'all',
        genre: params.genre || 'all',
        // 如果是点击热播榜，sortBy为plays；如果是点击类型精选，默认也按热播排
        sortBy: params.sortBy || 'plays',
      };

      this.$router.push({
        name: 'Ranking', // 假设排行榜页路由名为 Ranking
        query: queryParams
      });
    },

    // ... 在 methods 对象中 ...

    /**
     * 将后端返回的单个电影对象转换为前端需要的格式
     * @param {object} movie - 后端返回的原始电影对象
     * @returns {object} - 前端格式的电影对象
     */
    mapMovieData(movie) {
      // [重要!] 你的后端数据中没有提供海报路径(poster_path)
      // 这里我们暂时使用一个占位符图片。你需要和后端确认如何获取海报图。
      // 可能是需要用 movie_id 再去请求，或者后端直接提供一个完整的URL。
      // 暂时用一个固定的占位图地址。
      const posterUrl = 'https://via.placeholder.com/500x750.png?text=' + movie.title; // 占位图

      // 安全地处理 genres 和 countries 字符串
      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      const movieCountries = movie.countries_Str ? movie.countries_Str.split(',').map(c => c.trim()) : [];

      return {
        id: movie.movie_id,
        title: movie.title,
        // [重要!] 使用占位图，因为后端没返回 poster_path
        posterUrl: posterUrl,
        // posterUrl: movie.poster_path ? `${IMAGE_BASE_URL}${movie.poster_path}` : 'path/to/default/poster.jpg', // 这是你原来的代码，会失败

        // 修正大小写问题
        isVip: movie.access_Level === 'VIP', // 注意是 access_Level

        // 修正大小写问题，并提供备用值
        playCount: movie.weekly_Popularity || movie.popularity || 0, // 注意是 weekly_Popularity

        // 修正大小写问题
        rating: movie.vote_Average || 0, // 注意是 vote_Average

        // 修正名称问题
        genres: movieGenres, // 从 genres_Str 解析

        // 修正名称问题
        region: movieCountries.length > 0 ? movieCountries[0] : '未知',

        // 后端没有 release_date，先用一个默认值，否则排序会出问题
        releaseDate: movie.release_date || '1970-01-01',
      };
    },

    /**
     * 从所有电影数据中动态生成类型和地区列表
     * @param {Array} movies - 处理过的电影数据列表
     */
    generateFilters(movies) {
      const genreSet = new Set();
      const regionSet = new Set();

      movies.forEach(movie => {
        movie.genres.forEach(g => genreSet.add(g));
        if(movie.region && movie.region !== '未知') {
          regionSet.add(movie.region);
        }
      });

      this.genres = [{ key: 'all', name: '全部类型' }, ...Array.from(genreSet).map(g => ({ key: g, name: g }))];
      this.regions = [{ key: 'all', name: '全部地区' }, ...Array.from(regionSet).map(r => ({ key: r, name: r }))];
    },

    /**
     * 获取并处理所有主页数据
     */
    async fetchData() {
      this.loading = true;
      try {
        // 只调用一个接口，获取按热度排好序的所有电影
        // 假设 movieService.getList() 已被修改为请求 /movie/hotmovies
        const rawMovies = await movieService.getList();

        // 映射所有电影数据，结果本身就是按热度排序的
        this.allMovies = rawMovies.map(this.mapMovieData);

        // 基于获取的数据，动态生成筛选器列表
        this.generateFilters(this.allMovies);

      } catch (error) {
        console.error("获取首页数据失败:", error);
        // 你可以在这里设置一个错误状态，在UI上显示错误信息
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
.loading-container{display:flex;flex-direction:column;justify-content:center;align-items:center;height:calc(100vh - 68px);color:#8c8c8c}.loading-spinner{border:5px solid #333;border-top:5px solid #e50914;border-radius:50%;width:50px;height:50px;animation:spin 1s linear infinite;margin-bottom:1rem}@keyframes spin{0%{transform:rotate(0)}100%{transform:rotate(360deg)}}.hero{position:relative;height:calc(100vh - 68px);min-height:500px;background-size:cover;background-position:center top;display:flex;align-items:center;padding:0 3.5rem}.btn-login{position:absolute;top:1.5rem;right:3.5rem;z-index:10;background:#e50914;color:#fff;padding:.5rem 1.2rem}.hero-content{position:relative;z-index:5;max-width:40%}.hero-title{font-size:4rem;font-weight:700;margin-bottom:1.5rem;text-shadow:2px 2px 4px rgba(0,0,0,.45)}.hero-description{font-size:1.2rem;line-height:1.5;margin-bottom:2rem;max-width:500px;text-shadow:1px 1px 2px rgba(0,0,0,.7)}.hero-fade{position:absolute;bottom:0;left:0;width:100%;height:10rem;background-image:linear-gradient(to top,#141414,transparent)}.hero-buttons{display:flex;gap:1rem}.btn{padding:.7rem 1.8rem;border:none;border-radius:4px;font-size:1.1rem;font-weight:700;cursor:pointer;display:flex;align-items:center;gap:.7rem;transition:opacity .3s}.btn:hover{opacity:.8}.btn-play{background:#fff;color:#000}.btn-list{background:rgba(109,109,110,.7);color:#fff}.content-rows{padding:0 3.5rem;margin-top:-8vh;position:relative;z-index:10}.row{margin-bottom:3rem}.row-title{font-size:1.4rem;font-weight:700;margin-bottom:1rem}.thumbnails-container{display:flex;overflow-x:auto;overflow-y:hidden;padding-bottom:1rem;-ms-overflow-style:none;scrollbar-width:none}.thumbnails-container::-webkit-scrollbar{display:none}.thumbnail{position:relative;flex:0 0 16.66%;padding:0 .2rem;transition:transform .3s ease;cursor:pointer}.thumbnail img{width:100%;border-radius:4px;display:block}.thumbnail:hover{transform:scale(1.15);z-index:20}.vip-badge{position:absolute;top:8px;left:8px;background-color:#e50914;color:#fff;padding:4px 8px;border-radius:4px;font-size:.8rem;font-weight:700;z-index:2;box-shadow:0 0 5px rgba(0,0,0,.5)}.row-title.clickable{cursor:pointer;display:inline-flex;align-items:center;transition:color .3s}.row-title.clickable:hover{color:#e50914}.view-all-arrow{font-size:1.8rem;margin-left:.5rem;font-weight:400;transition:transform .3s}.row-title.clickable:hover .view-all-arrow{transform:translateX(5px)}
</style>
