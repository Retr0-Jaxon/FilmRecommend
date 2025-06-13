// src/App.vue

<template>
  <div id="app">
    <!-- router-view 是一个占位符，它会渲染当前路由匹配的组件 -->
    <!-- 这里不应该有 HomePage 或 RankingPage 的标签 -->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 全局样式 */
body {
  margin: 0;
  background-color: #141414;
}
</style>