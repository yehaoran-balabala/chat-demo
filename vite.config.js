import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
    plugins: [vue()],

    // 设置根目录为 frontend
    root: 'frontend',

    // 输出目录设置为 SpringBoot 的静态资源目录
    build: {
        outDir: '../src/main/resources/static',
        emptyOutDir: true,
        assetsDir: 'assets'
    },

    // 开发服务器配置
    server: {
        port: 3000,
        proxy: {
            // 代理 API 请求到 SpringBoot 后端
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            },
            '/messages': {
                target: 'http://localhost:8080',
                changeOrigin: true
            },
            '/stream': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    },

    // 路径别名
    resolve: {
        alias: {
            '@': resolve(__dirname, 'frontend/src'),
            '@components': resolve(__dirname, 'frontend/src/components'),
            '@pages': resolve(__dirname, 'frontend/src/pages'),
            '@utils': resolve(__dirname, 'frontend/src/utils')
        }
    },

    // 公共基础路径
    base: './'
})