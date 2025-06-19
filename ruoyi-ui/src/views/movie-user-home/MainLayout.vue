<!-- src/components/MainLayout.vue -->
<template>
  <div class="main-layout">
    <header role="navigation" aria-label="主导航栏" :class="['main-nav', { 'scrolled': isNavScrolled }]">
      <div class="nav-left">
        <router-link to="/" class="nav-logo">FilmRecommend</router-link>
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
          <template v-if="!isVip">
            <div class="common" @click="goToPayment" style="cursor: pointer;">点此成为VIP</div>
          </template>
          <template v-else>
            <div class="vip">你已经是vip了</div>
          </template>
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
import {getInfo} from "@/api/login";
import axios from 'axios';



// console.log(this.$store.state.user.roles );


export default {
  name: 'MainLayout',
  data() {
    return { isNavScrolled: false,
    isLoggedIn:false,
      isVip:true,
      paying:false,
    };
  },
  methods: {
    async goToPayment() {
      // 这里假设你的后端有生成支付订单的接口
      // 1. 获取用户ID
      const userInfo = await getInfo(); // 确保等待异步完成
      const userId = userInfo.user.userId;

      // 2. 生成订单号
      const timestamp = new Date().getTime();
      const outTradeNo = `${userId}_${timestamp}`; // 用户ID_时间戳
        const url = `http://localhost:8080/alipay/pay?outTradeNo=${outTradeNo}&totalAmount=88.88&subject=VIPOrder`;
        axios.get(url)
        .then(response => {

        const paymentUrl = url


        // 跳转到支付宝沙箱支付页面
        window.location.href = paymentUrl




        // 或者在新窗口打开
        // window.open(paymentUrl, '_blank')
      }).catch(error => {
        console.error('支付请求失败:', error)
        this.$message.error('支付请求失败，请稍后重试')
      })
    },
    handleScroll: throttle(function() {
      this.isNavScrolled = window.scrollY > 10;
    }, 100),
    checkLoginStatus() {
      this.isLoggedIn = !!getToken();
      if (this.isLoggedIn) {
        this.checkVipStatus();  // 如果已登录，检查VIP状态
      } else {
        this.isVip = false;  // 未登录则重置VIP状态
      }
    },
    async checkVipStatus() {
      try {
        const response = await getInfo();
        // 假设返回数据结构：{ user: { roles: [{ roleId: 数字 }] } }
        this.isVip = response.user.roles.some(role => role.roleId === 101);
      } catch (error) {
        console.error('检查VIP状态出错:', error);
        this.isVip = false;
      }
    },
    handleLogout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          this.isLoggedIn=false;
          this.isVip=false;
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
.common {
  padding: 8px 16px;
  background: linear-gradient(135deg, #ffd700, #ff9500);
  color: white;
  border-radius: 4px;
  font-weight: bold;
  transition: all 0.3s;
}

.common:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.vip{
  background-image: linear-gradient(to right, gold, #b400ff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: bold;
}
/* 确保内容区域不会被固定的导航栏遮挡 */
.content-area {
  padding-top: 68px;
}
</style>
