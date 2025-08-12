// 简单路由系统
const router = {
    currentRoute: 'home',
    routes: ['home', 'chat', 'history', 'profile', 'help'],

    push(route) {
        if (this.routes.includes(route)) {
            this.currentRoute = route
            window.location.hash = route
            window.$emit(window.EVENT_NAMES.PAGE_CHANGE, route)
        }
    },

    initFromHash() {
        const hash = window.location.hash.slice(1)
        if (this.routes.includes(hash)) {
            this.currentRoute = hash
            return hash
        }
        return 'home'
    }
}

// 监听哈希变化
window.addEventListener('hashchange', () => {
    const route = router.initFromHash()
    window.$emit(window.EVENT_NAMES.PAGE_CHANGE, route)
})

window.router = router