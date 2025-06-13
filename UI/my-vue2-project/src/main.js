// src/main.js

import Vue from 'vue'
import App from './App.vue'
import router from './router' // 确保引入了路由配置

Vue.config.productionTip = false

new Vue({
  router, // 确保将 router 实例注入到 Vue 实例中
  render: h => h(App)
}).$mount('#app')