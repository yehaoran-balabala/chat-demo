// 本地存储工具
const storage = {
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value))
            return true
        } catch (error) {
            console.error('存储数据失败:', error)
            return false
        }
    },

    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key)
            return item ? JSON.parse(item) : defaultValue
        } catch (error) {
            console.error('读取数据失败:', error)
            return defaultValue
        }
    },

    remove(key) {
        try {
            localStorage.removeItem(key)
            return true
        } catch (error) {
            console.error('删除数据失败:', error)
            return false
        }
    },

    clear() {
        try {
            localStorage.clear()
            return true
        } catch (error) {
            console.error('清空数据失败:', error)
            return false
        }
    }
}

// 全局方法
window.$setLocal = storage.set
window.$getLocal = storage.get
window.$removeLocal = storage.remove
window.$clearLocal = storage.clear
window.storage = storage