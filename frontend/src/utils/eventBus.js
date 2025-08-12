// 事件总线
const eventBus = {
    events: {},

    on(event, callback) {
        if (!this.events[event]) {
            this.events[event] = []
        }
        this.events[event].push(callback)
    },

    emit(event, data) {
        if (this.events[event]) {
            this.events[event].forEach(callback => callback(data))
        }
    },

    off(event, callback) {
        if (this.events[event]) {
            this.events[event] = this.events[event].filter(cb => cb !== callback)
        }
    }
}

// 全局事件名称
window.EVENT_NAMES = {
    PAGE_CHANGE: 'page:change',
    NOTIFICATION_SHOW: 'notification:show',
    NOTIFICATION_HIDE: 'notification:hide',
    MODAL_OPEN: 'modal:open',
    MODAL_CLOSE: 'modal:close',
    ERROR_GLOBAL: 'error:global',
    ERROR_API: 'error:api'
}

// 全局事件方法
window.$on = eventBus.on.bind(eventBus)
window.$emit = eventBus.emit.bind(eventBus)
window.$off = eventBus.off.bind(eventBus)