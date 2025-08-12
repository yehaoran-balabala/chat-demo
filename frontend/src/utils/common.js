// 通用工具函数
const utils = {
    // 生成唯一ID
    generateId(prefix = 'id') {
        return `${prefix}_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    },

    // 防抖函数
    debounce(func, wait) {
        let timeout
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout)
                func(...args)
            }
            clearTimeout(timeout)
            timeout = setTimeout(later, wait)
        }
    },

    // 节流函数
    throttle(func, limit) {
        let inThrottle
        return function() {
            const args = arguments
            const context = this
            if (!inThrottle) {
                func.apply(context, args)
                inThrottle = true
                setTimeout(() => inThrottle = false, limit)
            }
        }
    },

    // 格式化日期
    formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
        const d = new Date(date)
        const year = d.getFullYear()
        const month = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        const hours = String(d.getHours()).padStart(2, '0')
        const minutes = String(d.getMinutes()).padStart(2, '0')
        const seconds = String(d.getSeconds()).padStart(2, '0')

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds)
    },

    // 复制到剪贴板
    async copyToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text)
            return true
        } catch (error) {
            console.error('复制失败:', error)
            return false
        }
    }
}

window.utils = utils