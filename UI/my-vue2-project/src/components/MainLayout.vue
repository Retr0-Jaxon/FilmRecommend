<!-- src/components/MainLayout.vue -->

<template>
  <div class="main-layout">
    <!-- 这是所有页面共享的顶部导航栏 -->
    <header :class="['main-nav', { 'scrolled': isNavScrolled }]">
      <div class="nav-left">
        <!-- 点击 LOGO 返回首页 -->
        <router-link to="/" class="nav-logo">PILIPILI</router-link>
        <div class="nav-links nav-actions">
          <router-link to="/search">搜索</router-link>
          <!-- 点击 排行 跳转到排行榜页面 -->
          <router-link to="/ranking">排行</router-link>
        </div>
      </div>
      <div class="nav-right">
        <div class="profile-pic"></div>
      </div>
    </header>

    <!-- 主体内容区域：变化的页面（HomePage 或 RankingPage）会在这里渲染 -->
    <main class="content-area">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: 'MainLayout',
  data() {
    return {
      isNavScrolled: false,
    };
  },
  methods: {
    handleScroll() {
      // 这里的 10 是一个阈值，可以根据需要调整
      this.isNavScrolled = window.scrollY > 10;
    },
    showSearch() {
      alert("即将跳转到 '搜索' 页面...");
    },
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
};
</script>

<style scoped>
/* 这里是导航栏的专属样式，从 HomePage 移动过来 */
.main-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 3.5rem;
  z-index: 100;
  background-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.7) 10%, rgba(0, 0, 0, 0));
  transition: background-color 0.4s ease-in-out;
}

.main-nav.scrolled {
  background-color: #141414;
}

.nav-left, .nav-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-logo {
  color: #e50914;
  font-size: 1.8rem;
  font-weight: bold;
  text-decoration: none;
}

.nav-links a {
  color: #e5e5e5;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
  padding: 0.5rem;
  border-radius: 4px;
}

.nav-links a:hover,
.nav-links .router-link-exact-active { /* 高亮当前激活的路由链接 */
  color: #fff;
  font-weight: bold;
}

.profile-pic {
  width: 32px;
  height: 32px;
  background-color: #0073e6;
  border-radius: 4px;
  cursor: pointer;
}

/* 确保内容区域不会被固定的导航栏遮挡 */
.content-area {
  padding-top: 68px; /* 这个值约等于导航栏的高度 */
}
</style>