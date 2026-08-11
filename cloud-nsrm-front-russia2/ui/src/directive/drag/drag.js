export default {
  bind (el, binding, vnode) {
    const dialogHeaderEl = el.querySelector(binding.value.dragHandle) // 触发拖拽的div
    const dragDom = el

    dialogHeaderEl.style.cssText += ';cursor:move;'

    dialogHeaderEl.onmousedown = (e) => {
      // 鼠标按下，计算当前元素距离可视区的距离
      const disX = e.clientX - dragDom.offsetLeft
      const disY = e.clientY - dragDom.offsetTop
      document.onmousemove = function (e) {
        // 通过事件委托，计算移动的距离
        let l = e.clientX - disX
        let t = e.clientY - disY
        dragDom.style.left = l + 'px'
        dragDom.style.top = t + 'px'
      }

      document.onmouseup = function (e) {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
}
