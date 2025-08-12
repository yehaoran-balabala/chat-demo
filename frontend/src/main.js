import { createApp } from 'vue'
import App from './App.vue'

// 导入样式
import './styles/main.css'

// 导入工具函数
import './utils/eventBus.js'
import './utils/storage.js'
import './utils/common.js'
import './utils/api.js'

// 导入路由
import './router.js'

console.log('正在初始化Vue应用...')

try {
    // 创建Vue应用
    const app = createApp(App)

    // 全局属性
    app.config.globalProperties.$router = window.router
    app.config.globalProperties.$utils = window.utils
    app.config.globalProperties.$storage = window.storage
    app.config.globalProperties.$api = window.api
    app.config.globalProperties.$chatApi = window.chatApi

    // 全局错误处理
    app.config.errorHandler = (error, instance, info) => {
        console.error('Vue应用错误:', error, info)

        window.$emit(window.EVENT_NAMES.ERROR_GLOBAL, {
            error,
            instance,
            info,
            type: 'vue_error'
        })
    }

    // 挂载应用
    app.mount('#app')

    console.log('Vue应用初始化完成')

} catch (error) {
    console.error('应用初始化失败:', error)

    // 显示错误信息
    document.getElementById('app').innerHTML = `
        <div style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 2rem;
            text-align: center;
            font-family: 'Inter', sans-serif;
        ">
            <div style="
                background: #fee;
                border: 1px solid #fcc;
                border-radius: 8px;
                padding: 2rem;
                max-width: 500px;
                width: 100%;
            ">
                <h2 style="color: #c33; margin: 0 0 1rem 0;">
                    <i class="fas fa-exclamation-triangle" style="margin-right: 0.5rem;"></i>
                    应用启动失败
                </h2>
                <p style="margin: 0 0 1.5rem 0; color: #666;">
                    很抱歉，心理健康AI助手在启动过程中遇到了技术问题。
                    请尝试刷新页面，或检查您的网络连接。
                </p>
                <button 
                    onclick="location.reload()"
                    style="
                        background: #007bff;
                        color: white;
                        border: none;
                        padding: 0.75rem 1.5rem;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 0.9rem;
                    "
                >
                    <i class="fas fa-refresh" style="margin-right: 0.5rem;"></i>
                    重新加载页面
                </button>
            </div>
        </div>
    `
}