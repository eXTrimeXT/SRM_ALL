import cookies from 'js-cookie'
import { isIE, isIE11 } from 'lib@/utils/validate'
import { domain } from '@/config/sysConfig'
// import dayjs from 'dayjs'
// import dayjs from "lib@/plugins/dayjs";

/**
 * 退出登录
 */
export function logout () {
  if (window.location.hash !== '#/login') {
    setTimeout(() => {
      clearStorage()
      location.hostname === 'gsrm.midea.com'
        ? (location.href = 'https://gsrm.midea.com/#/login')
        : (location.href = location.origin + '#/login')
    }, 0)
  }
}
/**
 * 判断是否登录
 * @param {*} router 路由
 */
export function checkLogin (router) {
  router.beforeEach((to, from, next) => {
    next()
  })
}

export function parseTime (time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const date = new Date(time)
  let fmt = cFormat || 'yyyy-MM-dd hh:mm:ss'
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3) // 季度
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(
      RegExp.$1,
      (date.getFullYear() + '').substr(4 - RegExp.$1.length)
    )
  }
  Object.keys(o).forEach(k => {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  })
  return fmt
}

/**
 * 清除localstorage,sessionStorage,cookie
 */
export function clearStorage () {
  localStorage.clear()
  sessionStorage.clear()
  clearCookie()
}

/**
 * 清除所有cookie
 */
export const clearCookie = () => {
  // eslint-disable-next-line no-useless-escape
  var keys = document.cookie.match(/[^ =;]+(?=\=)/g)
  if (keys) {
    for (var i = keys.length; i--;) {
      cookies.remove(keys[i], {
        expires: new Date(0).toUTCString()
      })
      cookies.remove(keys[i], {
        expires: new Date(0).toUTCString(),
        domain: getDomain()
      })
    }
  }
}

/**
 * 获取主域
 * @param weburl
 * @returns {string}
 */
export const getDomain = (host = domain()) => {
  const hostExts = ['.com', '.cn', '.net', '.cc', '.sh', '.org']
  const ext = []
  let reExt
  let exist = false
  for (var i = 0; i < hostExts.length; i++) {
    if (host.indexOf(hostExts[i]) !== -1) {
      ext[ext.length] = hostExts[i]
      reExt = new RegExp('\\' + hostExts[i])
      host = host.replace(reExt, '{' + (ext.length - 1) + '}')
      exist = true
    } else {
      break
    }
  }
  if (!exist) {
    return host
  }
  var hostarray = host.split('.')
  host = hostarray[hostarray.length - 1]
  for (let i = 0; i < ext.length; i++) {
    reExt = new RegExp('\\{' + i + '\\}')
    host = host.replace(reExt, ext[i])
  }
  return host
}

/**
 *
 * @param {Element} _element
 * @returns {undefined}
 */
export function removeElement (_element) {
  if (isIE11() || isIE()) {
    _element.removeNode(true)
  } else {
    const _parentElement = _element.parentNode
    if (_parentElement) {
      // console.log('[有父节点]', _element)
      _parentElement.removeChild(_element)
    }
    // console.log('[无父节点]', _element)
  }
}

/**
 *
 * @param {string|number} offset
 * @returns {number}
 */
export function getTextWidth (text) {
  let width = 0
  let html = document.createElement('span')
  html.innerText = text
  html.className = 'getTextWidth'
  document.querySelector('body').appendChild(html)
  width = document.querySelector('.getTextWidth').offsetWidth
  // document.querySelector(".getTextWidth").remove();
  const geTextWidths = document.querySelector('.getTextWidth')
  if (geTextWidths.length) {
    Array.from(geTextWidths).forEach(i => removeElement(i))
  } else {
    console.log('[geTextWidths]', geTextWidths)
    removeElement(geTextWidths)
  }
  console.log(`[${text}]: ${width}`)
  return `${width}`
}
