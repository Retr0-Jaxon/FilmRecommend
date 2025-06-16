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
      <template v-if="!isLoggedIn" >
        <div class="nav-right">
          <router-link to="/register" class="register" aria-label="注册">注册</router-link>
          <router-link to="/login" class="login" aria-label="登录">登录</router-link>
        </div>

      </template>
      <template v-else >
        <div class="nav-right">
          <button class="logout" @click="handleLogout" aria-label="退出登录">退出登录</button>
        </div>

      </template>
    </header>

    <main class="content-area">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import { throttle } from 'lodash-es';
import {getToken, removeToken} from "@/utils/auth";

export default {
  name: 'MainLayout',
  data() {
    return { isNavScrolled: false,
    isLoggedIn:false
    };
  },
  methods: {
    handleScroll: throttle(function() {
      this.isNavScrolled = window.scrollY > 10;
    }, 100),
    checkLoginStatus() {
      this.isLoggedIn = !!getToken(); // 检查是否有token
    },
    handleLogout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '';
        })
      }).catch(() => {});
    }
  },
  mounted() {
    if (typeof window !== 'undefined') {
      window.addEventListener('scroll', this.handleScroll);
    }
    this.checkLoginStatus();
  },
  beforeDestroy() {
    if (typeof window !== 'undefined') {
      window.removeEventListener('scroll', this.handleScroll);
    }
  },
  watch: {
    // 监听路由变化，检查登录状态
    '$route': 'checkLoginStatus'
  }
};
</script>

<style scoped>
.main-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem 1rem 3.5rem;
  z-index: 100;
  background-color: white;
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
  color: white !important;
}

.nav-links a:hover,
.nav-links .router-link-exact-active {
  color: #e50914;
  font-weight: bold;
}

.nav-right a, .nav-right button {
  display: inline-block;
  width: 5rem;
  height: 32px;
  line-height: 32px;
  text-align: center;
  background-color: #e50914;
  border-radius: 4px;
  cursor: pointer;
  border-width: 0;
  color: white;
  text-decoration: none;
}

.nav-right a:hover, .nav-right button:hover {
  opacity: 0.8;
}

.register {
  margin-right: 2rem;
  background: #e50914;
}
.login{
  background: #e50914;
}

/* 确保内容区域不会被固定的导航栏遮挡 */
.content-area {
  padding-top: 68px;
}
</style>
