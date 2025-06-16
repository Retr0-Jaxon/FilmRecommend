<template>
  <div class="home-page-container">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载影片...</p>
    </div>

    <main v-else>
      <!-- 主视觉区 -->
      <section class="hero" :style="{ backgroundImage: 'url(' + heroContent.backgroundImage + ')' }">
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
            <!-- 【修改】添加点击事件以跳转到详情页 -->
            <div class="thumbnail" v-for="item in hotMovies" :key="item.id" @click="goToDetailPage(item.id)">
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
            <!-- 【修改】添加点击事件以跳转到详情页 -->
            <div class="thumbnail" v-for="item in moviesByGenre[genre.key]" :key="item.id" @click="goToDetailPage(item.id)">
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
            <!-- 【修改】添加点击事件以跳转到详情页 -->
            <div class="thumbnail" v-for="item in moviesByRegion[region.key]" :key="item.id" @click="goToDetailPage(item.id)">
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
import axios from 'axios';
import { movieService } from '../services/movieService';

export default {
  name: 'HomePage',
  data() {
    return {
      loading: true,
      heroContent: {
        title: '纸房子',
        description: '八名盗贼将自己与人质反锁在西班牙皇家造币厂内，他们背后的犯罪首脑则试图操纵警察实现自己的计划…',
        backgroundImage: 'https://image.tmdb.org/t/p/w500/pA4sNvco71n729r5n5Y1xGDHnS6.jpg',
      },
      allMovies: [],
      genres: [],
      regions: [],
    };
  },
  computed: {
    hotMovies() {
      return this.allMovies.slice(0, 10);
    },
    moviesByGenre() {
      const grouped = {};
      this.genres.forEach(genre => {
        if (genre.key !== 'all') {
          grouped[genre.key] = this.allMovies
            .filter(movie => movie.genres.includes(genre.key))
            .sort((a, b) => b.playCount - a.playCount)
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
            .sort((a, b) => b.rating - a.rating)
            .slice(0, 10);
        }
      });
      return grouped;
    }
  },
  methods: {
    goToLoginPage(){
      this.$router.push({ name: 'Login' });
    },
    goToRankingPage(params) {
      const queryParams = {
        time: params.timeRange || 'all',
        level: params.level || 'all',
        genre: params.genre || 'all',
        sortBy: params.sortBy || 'plays',
      };
      this.$router.push({
        name: 'Ranking',
        query: queryParams
      });
    },

    /**
     * 【新增】跳转到电影详情页
     * @param {number} movieId - 电影的ID
     */
    goToDetailPage(movieId) {
      // 确保你的详情页路由名为 'MovieDetail'
      this.$router.push({ name: 'MovieDetail', params: { id: movieId } });
    },

    async mapMovieData(movie) {
      const posterUrl = await movieService.getMoviePosterUrl(movie.movie_id); // 假设后端接口需要 movieId

      console.log(posterUrl);

      const movieGenres = movie.genres_Str ? movie.genres_Str.split(',').map(g => g.trim()) : [];
      const movieCountries = movie.countries_Str ? movie.countries_Str.split(',').map(c => c.trim()) : [];

      return {
        id: movie.movie_id,
        title: movie.title,
        posterUrl: posterUrl,
        isVip: movie.access_Level >= 1, // 假设 access_Level > 1 就是VIP
        playCount: movie.weekly_Popularity || movie.popularity || 0,
        rating: movie.vote_Average || 0,
        genres: movieGenres,
        region: movieCountries.length > 0 ? movieCountries[0] : '未知',
        releaseDate: movie.release_date || '1970-01-01',
      };
    },
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
    async fetchData() {
      this.loading = true;
      try {
        const rawMovies = await movieService.getList();
        const mappedMoviesPromises = rawMovies.map(this.mapMovieData);
        const mappedMovies = await Promise.all(mappedMoviesPromises);
        // console.log(mappedMovies);

        const sortedMovies = mappedMovies.sort((a, b) => b.playCount - a.playCount);
        this.allMovies = sortedMovies;
        this.generateFilters(this.allMovies);
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
.loading-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 68px);
  color: #8c8c8c;
}

.loading-spinner {
  border: 5px solid #333;
  border-top: 5px solid #e50914;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}

.hero {
  position: relative;
  height: calc(100vh - 68px);
  min-height: 500px;
  background-size: cover;
  background-position: center top;
  display: flex;
  align-items: center;
  padding: 0 3.5rem;
  background-color: black;
  color: white;
}

.btn-login {
  position: absolute;
  top: 1.5rem;
  right: 3.5rem;
  z-index: 10;
  background: #e50914;
  color: #fff;
  padding: 0.5rem 1.2rem;
}

.hero-content {
  position: relative;
  z-index: 5;
  max-width: 40%;

}

.hero-title {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.45);
}

.hero-description {
  font-size: 1.2rem;
  line-height: 1.5;
  margin-bottom: 2rem;
  max-width: 500px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.hero-fade {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 10rem;
  background-image: linear-gradient(to top, #141414, transparent);
}

.hero-buttons {
  display: flex;
  gap: 1rem;
}

.btn {
  padding: 0.7rem 1.8rem;
  border: none;
  border-radius: 4px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.7rem;
  transition: opacity 0.3s;
}

.btn:hover {
  opacity: 0.8;
}

.btn-play {
  background: #fff;
  color: #000;
}

.btn-list {
  background: rgba(109, 109, 110, 0.7);
  color: #fff;
}

.content-rows {
  padding: 0 3.5rem;
  margin-top: -8vh;
  position: relative;
  z-index: 10;
  background-color: black;
  color: white;
}

.row {
  margin-bottom: 3rem;
}

.row-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.thumbnails-container {
  display: flex;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 1.5rem 0; /* 增加上下内边距 */
  -ms-overflow-style: none;
  scrollbar-width: none;
  scroll-padding: 0 20px;
  //gap: 1.5rem; /* 增加图片间距 */
}

.thumbnail {
  position: relative;
  flex: 0 0 auto; /* 缩小图片宽度占比 */
  padding: 0;
  transition: transform 0.3s ease;
  cursor: pointer;
  width: 200px;
  margin-right: 50px; /* 增加右侧间距 */
}

.thumbnail:first-child{
  margin-left: 15px;
}
.thumbnail img {
  width: 100%;
  height:auto;
  border-radius: 4px;

  aspect-ratio: 2/3; /* 保持图片比例 */
}

.thumbnail:hover {
  transform: scale(1.1); /* 调整放大比例 */
  z-index: 20;

}

.vip-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: #e50914;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 700;
  z-index: 2;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.row-title.clickable {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  transition: color 0.3s;
}

.row-title.clickable:hover {
  color: #e50914;
}

.view-all-arrow {
  font-size: 1.8rem;
  margin-left: 0.5rem;
  font-weight: 400;
  transition: transform 0.3s;
}

.row-title.clickable:hover .view-all-arrow {
  transform: translateX(5px);
}

</style>
