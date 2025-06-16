<!-- src/components/MainLayout.vue -->
<template>
  <div class="main-layout">
    <header role="navigation" aria-label="主导航栏" :class="['main-nav', { 'scrolled': isNavScrolled }]">
      <div class="nav-left">
        <router-link to="/" class="nav-logo">PILIPILI</router-link>
        <nav class="nav-links" aria-label="主导航链接">
          <router-link to="/search">搜索</router-link>
          <router-link to="/ranking">排行</router-link>
        </nav>
      </div>
      <div class="nav-right">
        <button class="profile-pic" aria-label="用户头像">

        </button>
      </div>
    </header>

    <main class="content-area">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import { throttle } from 'lodash-es';

export default {
  name: 'MainLayout',
  data() {
    return { isNavScrolled: false };
  },
  methods: {
    handleScroll: throttle(function() {
      this.isNavScrolled = window.scrollY > 10;
    }, 100),
  },
  mounted() {
    if (typeof window !== 'undefined') {
      window.addEventListener('scroll', this.handleScroll);
    }
  },
  beforeDestroy() {
    if (typeof window !== 'undefined') {
      window.removeEventListener('scroll', this.handleScroll);
    }
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
  //background-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.7) 10%, rgba(0, 0, 0, 0));
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
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
  padding: 0.5rem;
  border-radius: 4px;
}
.main-nav.scrolled .nav-links a {
  color: white !important; /* 强制覆盖默认颜色 */
}


.nav-links a:hover,
.nav-links .router-link-exact-active { /* 高亮当前激活的路由链接 */
  color: #e50914;
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
