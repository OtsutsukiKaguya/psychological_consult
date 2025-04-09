import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})

// import { fileURLToPath, URL } from 'node:url'
// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'

// // https://vite.dev/config/
// export default defineConfig({
//   plugins: [
//     vue(),
//     vueDevTools(),
//   ],
//   resolve: {
//     alias: {
//       '@': fileURLToPath(new URL('./src', import.meta.url))
//     },
//   },
//   server: {
//     proxy: {
//       // 将所有以 /api 开头的请求代理到后端服务器
//       '/api': {
//         target: 'http://localhost:8088',  // 后端 API 地址
//         changeOrigin: true,               // 是否修改请求头中的 Origin 字段
//         rewrite: (path) => path.replace(/^\/api/, ''), // 去掉路径中的 /api 前缀
//       },
//     },
//   },
// })