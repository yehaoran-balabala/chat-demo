// API 工具
const api = {
    baseURL: '',

    async request(url, options = {}) {
        try {
            const response = await fetch(this.baseURL + url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error('API请求失败:', error)
            window.$emit(window.EVENT_NAMES.ERROR_API, { error, url, options })
            throw error
        }
    },

    get(url, options = {}) {
        return this.request(url, { method: 'GET', ...options })
    },

    post(url, data, options = {}) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data),
            ...options
        })
    }
}

// 聊天API
const chatApi = {
    async getMessages() {
        return api.get('/messages')
    },

    async sendMessage(prompt) {
        return api.get(`/stream?prompt=${encodeURIComponent(prompt)}`)
    }
}

window.api = api
window.chatApi = chatApi