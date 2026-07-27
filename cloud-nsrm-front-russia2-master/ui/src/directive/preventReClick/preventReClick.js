// 防止按钮多次点击多次触发事件调用
export default {
  inserted: (el, binding) => {
    el.addEventListener('click', () => {
      if (!el.disabled) {
        el.disabled = true
        setTimeout(() => {
          el.disabled = false
        }, binding.value || 1000) // 默认重复点击时长1s
      }
    })
  }
}
