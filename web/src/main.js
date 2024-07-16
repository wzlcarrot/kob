import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// 创建 Pinia 实例
const pinia = createPinia()

// 创建 Vue 应用实例
const app = createApp(App)

// 使用 Pinia 和 Vue Router
app.use(pinia).use(router)

// 挂载 Vue 应用实例
app.mount('#app')