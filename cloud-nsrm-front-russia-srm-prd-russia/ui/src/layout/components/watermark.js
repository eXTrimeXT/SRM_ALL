'use strict'

let watermark = {}

const getDate = () => {
  const nowDate = new Date()
  const year = nowDate.getFullYear()
  const month =
    nowDate.getMonth() + 1 < 10
      ? '0' + (nowDate.getMonth() + 1)
      : nowDate.getMonth() + 1
  const day =
    nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
  const dateStr = year + '-' + month + '-' + day
  return dateStr
}

let setWatermark = str => {
  let id = '1.23452384164.123412415'

  if (document.getElementById(id) !== null) {
    document.body.removeChild(document.getElementById(id))
  }

  let can = document.createElement('canvas')
  can.width = 280
  can.height = 120

  let cans = can.getContext('2d')
  cans.rotate((-20 * Math.PI) / 180)
  cans.font = '14px Vedana'
  cans.fillStyle = 'rgba(118, 118, 118, 0.20)'
  // cans.fillStyle = "blue";
  cans.textAlign = 'left'
  cans.textBaseline = 'Middle'
  cans.fillText(str, can.width / 20, can.height - 30)
  if (str) { // 字符串不为空的时候才一起设置日期
    const date = getDate()
    cans.fillText(date, can.width / 20, can.height)
  }
  let div = document.createElement('div')
  div.id = id
  div.style.pointerEvents = 'none'
  div.style.top = '10px'
  div.style.left = '0px'
  div.style.position = 'fixed'
  div.style.zIndex = '100000'
  div.style.width = document.documentElement.clientWidth + 'px'
  div.style.height = document.documentElement.clientHeight + 'px'
  div.style.background =
    'url(' + can.toDataURL('image/png') + ') left top repeat'
  document.body.appendChild(div)
  return id
}

// 该方法只允许调用一次
watermark.set = str => {
  let id = setWatermark(str)
  setInterval(() => {
    if (document.getElementById(id) === null) {
      id = setWatermark(str)
    }
  }, 500)
  window.onresize = () => {
    setWatermark(str)
  }
}

export default watermark
