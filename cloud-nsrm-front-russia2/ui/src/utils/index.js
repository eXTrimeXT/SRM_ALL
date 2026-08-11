/**
 * Created by zhaomz1
 */
import store from '@/store'
import i18n from '@/lang'
import { greenStatus, invalidStatus, orangeStatus, redStatus } from '@/utils/statusMap'

// 判断详情非渲染引擎的时候format的值分是否俄罗斯,返回的是单单的时间格式并非返回实际内容
export const formatDatePicker = localStorage.getItem('cloudDateFormat') + `  [${localStorage.getItem('cloudTimeZoneFormat')}]`

export const formatDatePickerTime = localStorage.getItem('cloudDateTimeFormat') + `  [${localStorage.getItem('cloudTimeZoneFormat')}]`

// 传参为yyyy-mm-dd的日期格式获取本年第几周
export function getWeekNum (val) {
  /**
   * dateNow是当前日期
   * dateFirst是当年第一天
   * dataNumber是当前日期是今年第多少天
   * 用dataNumber + 当前年的第一天的周差距的和在除以7就是本年第几周
   */
  let dateNow = new Date(val)
  let dateFirst = new Date(val.slice(0, 4) + '-' + '01' + '-' + '01')
  let dataNumber = Math.round((dateNow.valueOf() - dateFirst.valueOf()) / 86400000)
  return Math.ceil((dataNumber + (dateFirst.getDay() + 1 - 1)) / 7)
}

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime (time, cFormat, isOldTime) {
  if (arguments.length === 0) {
    return null
  }
  if (!time) {
    return ''
  }
  let typeOne = ''
  // typeOne = '{d}.{m}.{y} {h}:{i}:{s}'
  if (isOldTime) {
    typeOne = cFormat
  } else {
    if (['{d}.{m}.{y} {h}:{i}:{s}', '{d}-{m}-{y} {h}:{i}:{s}'].includes(cFormat)) {
      const cloudDateTimeFormat = localStorage.getItem('cloudDateTimeFormat') || 'yyyy-MM-dd'
      typeOne = cloudDateTimeFormat.replace('dd', '{d}')
      typeOne = typeOne.replace('MM', '{m}')
      typeOne = typeOne.replace('yyyy', '{y}')
      typeOne = typeOne.replace('HH', '{h}')
      typeOne = typeOne.replace('mm', '{i}')
      typeOne = typeOne.replace('ss', '{s}')
    } else {
      const cloudDateFormat = localStorage.getItem('cloudDateFormat') || 'yyyy-MM-dd'
      typeOne = cloudDateFormat.replace('dd', '{d}')
      typeOne = typeOne.replace('MM', '{m}')
      typeOne = typeOne.replace('yyyy', '{y}')
    }
  }

  const format = typeOne
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
      time = parseInt(time)
    }
    if (typeof time === 'number' && time.toString().length === 10) {
      time = time * 1000
    }
    if (time && String(time).includes('-')) {
      time = String(time).replace(/-/g, '/') // 兼容ie
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  // eslint-disable-next-line camelcase
  let timeStr = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    return value.toString().padStart(2, '0')
  })
  if (!isOldTime) {
    timeStr = timeStr + ' ' + localStorage.getItem('cloudTimeZoneFormat')
  }
  return timeStr
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime (time, option) {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() + 1 + '月' + d.getDate() + '日' + d.getHours() + '时' + d.getMinutes() + '分'
    )
  }
}

/**
 * 时间戳转化为年 月 日 时 分 秒
 * number: 传入时间戳
 * format：返回格式，支持自定义，但参数必须与formateArr里保持一致
 */

// 数据转化

export function formatTimeToDate (timeStamp, format) {
  if (!timeStamp) return '-'
  let pattern = new RegExp('[\u4E00-\u9FA5]+')
  if (pattern.test(timeStamp)) {
    // 中文
    return timeStamp
  } else {
    let formatNumber = n => {
      n = n.toString()
      return n[1] ? n : '0' + n
    }
    var formateArr = ['Y', 'M', 'D', 'h', 'm', 's']
    var returnArr = []
    var date = new Date(timeStamp)
    returnArr.push(date.getFullYear())
    returnArr.push(formatNumber(date.getMonth() + 1))
    returnArr.push(formatNumber(date.getDate()))

    returnArr.push(formatNumber(date.getHours()))
    returnArr.push(formatNumber(date.getMinutes()))
    returnArr.push(formatNumber(date.getSeconds()))

    for (var i in returnArr) {
      format = format.replace(formateArr[i], returnArr[i])
    }
    return format
  }
}
/**
 * @param {string} url
 * @returns {Object}
 */
export function getQueryObject (url) {
  url = url === null ? window.location.href : url
  const search = url.substring(url.lastIndexOf('?') + 1)
  const obj = {}
  const reg = /([^?&=]+)=([^?&=]*)/g
  search.replace(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1)
    let val = decodeURIComponent($2)
    val = String(val)
    obj[name] = val
    return rs
  })
  return obj
}

/**
 * @param {string} input value
 * @returns {number} output value
 */
export function byteLength (str) {
  // returns the byte length of an utf8 string
  let s = str.length
  for (var i = str.length - 1; i >= 0; i--) {
    const code = str.charCodeAt(i)
    if (code > 0x7f && code <= 0x7ff) s++
    else if (code > 0x7ff && code <= 0xffff) s += 2
    if (code >= 0xdc00 && code <= 0xdfff) i--
  }
  return s
}

/**
 * @param {Array} actual
 * @returns {Array}
 */
export function cleanArray (actual) {
  const newArray = []
  for (let i = 0; i < actual.length; i++) {
    if (actual[i]) {
      newArray.push(actual[i])
    }
  }
  return newArray
}

/**
 * @param {Object} json
 * @returns {Array}
 */
export function param (json) {
  if (!json) return ''
  return cleanArray(
    Object.keys(json).map(key => {
      if (json[key] === undefined) return ''
      return encodeURIComponent(key) + '=' + encodeURIComponent(json[key])
    }),
  ).join('&')
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj (url) {
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse(
    '{"' +
      decodeURIComponent(search)
        .replace(/"/g, '\\"')
        .replace(/&/g, '","')
        .replace(/=/g, '":"')
        .replace(/\+/g, ' ') +
      '"}',
  )
}

/**
 * @param {string} val
 * @returns {string}
 */
export function html2Text (val) {
  const div = document.createElement('div')
  div.innerHTML = val
  return div.textContent || div.innerText
}

/**
 * Merges two objects, giving the last one precedence
 * @param {Object} target
 * @param {(Object|Array)} source
 * @returns {Object}
 */
export function objectMerge (target, source) {
  if (typeof target !== 'object') {
    target = {}
  }
  if (Array.isArray(source)) {
    return source.slice()
  }
  Object.keys(source).forEach(property => {
    const sourceProperty = source[property]
    if (typeof sourceProperty === 'object') {
      target[property] = objectMerge(target[property], sourceProperty)
    } else {
      target[property] = sourceProperty
    }
  })
  return target
}

/**
 * @param {HTMLElement} element
 * @param {string} className
 */
export function toggleClass (element, className) {
  if (!element || !className) {
    return
  }
  let classString = element.className
  const nameIndex = classString.indexOf(className)
  if (nameIndex === -1) {
    classString += '' + className
  } else {
    classString =
      classString.substr(0, nameIndex) + classString.substr(nameIndex + className.length)
  }
  element.className = classString
}

/**
 * @param {string} type
 * @returns {Date}
 */
export function getTime (type) {
  if (type === 'start') {
    return new Date().getTime() - 3600 * 1000 * 24 * 90
  } else {
    return new Date(new Date().toDateString())
  }
}

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce (func, wait, immediate) {
  let timeout, args, context, timestamp, result

  const later = function () {
    // 据上一次触发时间间隔
    const last = +new Date() - timestamp

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args)
        if (!timeout) context = args = null
      }
    }
  }

  return function (...args) {
    context = this
    timestamp = +new Date()
    const callNow = immediate && !timeout
    // 如果延时不存在，重新设定延时
    if (!timeout) timeout = setTimeout(later, wait)
    if (callNow) {
      result = func.apply(context, args)
      context = args = null
    }

    return result
  }
}

/**
 * This is just a simple version of deep copy
 * Has a lot of edge cases bug
 * If you want to use a perfect deep copy, use lodash's _.cloneDeep
 * @param {Object} source
 * @returns {Object}
 */
export function deepClone (source) {
  if (!source || typeof source !== 'object') {
    throw new Error('error arguments', 'deepClone')
  }
  const targetObj = source.constructor === Array ? [] : {}
  Object.keys(source).forEach(keys => {
    if (source[keys] && typeof source[keys] === 'object') {
      targetObj[keys] = deepClone(source[keys])
    } else {
      targetObj[keys] = source[keys]
    }
  })
  return targetObj
}

/**
 * @param {Array} arr
 * @returns {Array}
 */
export function uniqueArr (arr) {
  return Array.from(new Set(arr))
}

/**
 * @returns {string}
 */
export function createUniqueString () {
  const timestamp = +new Date() + ''
  // const randomNum = parseInt((1 + Math.random()) * 65536) + ''
  const randomNum = parseInt((1 + window.crypto.getRandomValues(new Uint8Array(1)) * 0.001) * 65536) + ''
  return (+(randomNum + timestamp)).toString(32)
}

/**
 * Check if an element has a class
 * @param {HTMLElement} elm
 * @param {string} cls
 * @returns {boolean}
 */
export function hasClass (ele, cls) {
  return !!ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'))
}

/**
 * Add class to element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function addClass (ele, cls) {
  if (!hasClass(ele, cls)) ele.className += ' ' + cls
}

/**
 * Remove class from element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function removeClass (ele, cls) {
  if (hasClass(ele, cls)) {
    const reg = new RegExp('(\\s|^)' + cls + '(\\s|$)')
    ele.className = ele.className.replace(reg, ' ')
  }
}

/**
 * 去掉字段前后的空白
 * @param {string} str
 * @returns {Boolean}
 */
export function trimField (str) {
  return str.trim()
}

/*
添加通用的一些方法，判断数组，对象，类
 */
// object判断
export const isObject = data => {
  return Object.prototype.toString.call(data) === '[object Object]'
}

// array判断
export const isArray = data => {
  return Object.prototype.toString.call(data) === '[object Array]'
}
// 是否为空
export function isNull (val) {
  return !!(val === null || val === '' || typeof val === 'undefined')
}
/**
 * 两边去空
 * @param {*} val
 */
export function trim (val) {
  if (isNull(val)) {
    return ''
  }
  let reg = /^\s*|\s*$/g
  return val.toString().replace(reg, '')
}

// 根据lov的value值获取显示值
export function getLabelByValue (lov, language) {
  for (let item of lov) {
    if (item.language === language) {
      return item.languageName
    }
  }
  return language
}
// 适配字典数据 type传类型
export function adaptDictData (data, type = 'dict') {
  let arr = []
  if (data && data.length > 0) {
    if (type === 'dict') {
      // 字典
      data.forEach(element => {
        arr.push({
          id: element.dictItemId.toString(),
          value: element.dictItemCode.toString(),
          label: element.dictItemName,
          disabled: element.disabled,
          desc: element.itemDescription // 描述
        })
      })
    } else if (type === 'unit') {
      // 单位
      data.forEach(element => {
        arr.push({
          id: element.unitId.toString(),
          value: element.unitCode.toString(),
          label: element.unitName,
          disabled: element.disabled
        })
      })
    } else if (type === 'currency') {
      // 币种
      data.forEach(element => {
        arr.push({
          id: element.currencyId.toString(),
          value: element.currencyCode.toString(),
          label: element.currencyName,
          disabled: element.disabled
        })
      })
    } else if (type === 'tax') {
      // 税率 隆基
      // taxCode: 11
      // taxId: 7944546925740032
      // taxKey: "IN 11"
      data.forEach(element => {
        let label = element.taxName ? `${element.taxKey} / ${element.taxName}` : element.taxKey
        arr.push({
          id: element.taxId.toString(),
          value: element.taxKey.toString(), // 文字 区分税率唯一值 字典双向绑定的时候要用key区分
          label: label,
          key: element.taxCode.toString(), // 值 税率计算的时候需要通过key转化拿code值
          disabled: element.disabled
        })
      })
    } else if (type === 'province') {
      // 省
      data.forEach(element => {
        arr.push({
          id: element.provinceId,
          value: element.provinceId.toString(),
          label: element.province,
          disabled: element.disabled
        })
      })
    } else if (type === 'city') {
      // 市
      data.forEach(element => {
        arr.push({
          id: element.cityId,
          value: element.cityId.toString(),
          label: element.city,
          disabled: element.disabled
        })
      })
    }
  }
  return arr
}

// 字典单个转化
export function adaptDictItem (dataItem, dictCode) {
  if (dictCode === 'tax') {
    let label = dataItem.taxName ? `${dataItem.taxKey} / ${dataItem.taxName}` : dataItem.taxKey
    return {
      id: dataItem.taxId.toString(),
      value: dataItem.taxKey.toString(), // 文字 区分税率唯一值 字典双向绑定的时候要用key区分
      label: label,
      key: dataItem.taxCode.toString(), // 值 税率计算的时候需要通过key转化拿code值
      disabled: dataItem.enabled == 'N'
    }
  } else if (dictCode === 'unit') {
    return {
      id: dataItem.unitId.toString(),
      label: dataItem.unitName,
      value: dataItem.unitCode,
      disabled: dataItem.enabled == 'N'
    }
  } else if (dictCode === 'currency') {
    return {
      id: dataItem.currencyId.toString(),
      label: dataItem.currencyCode + ':' + dataItem.currencyName,
      value: dataItem.currencyCode,
      disabled: dataItem.enabled == 'N',
      currencyName: dataItem.currencyName
    }
  } else if (dictCode === 'province') {
    // 省
    return {
      id: dataItem.provinceId,
      value: dataItem.provinceId.toString(),
      label: dataItem.province,
      disabled: dataItem.disabled
    }
  } else if (dictCode === 'city') {
    // 市
    return {
      id: dataItem.cityId,
      value: dataItem.cityId.toString(),
      label: dataItem.city,
      disabled: dataItem.disabled
    }
  } else {
    return {
      id: dataItem.dictItemId.toString(),
      label: dataItem.dictItemName,
      value: dataItem.dictItemCode.toString(),
      disabled: dataItem.disabled,
      description: dataItem.itemDescription
    }
  }
}

// 字典根据value取label值
export function getDictLabelByValue (dataList, value) {
  for (let item of dataList) {
    if (item.value === value) {
      return item.label
    }
  }
  return value
}

/**
 * 时间戳转化为年 月 日 时 分 秒
 * number: 传入时间戳
 * format：返回格式，支持自定义，但参数必须与formateArr里保持一致
 */
export function formatDate (timeStamp, format) {
  if (!timeStamp) return '-'
  let pattern = new RegExp('[\u4E00-\u9FA5]+')
  if (pattern.test(timeStamp)) {
    // 中文
    return timeStamp
  } else {
    let formatNumber = n => {
      n = n.toString()
      return n[1] ? n : '0' + n
    }
    var formateArr = ['Y', 'M', 'D', 'h', 'm', 's']
    var returnArr = []
    var date = new Date(timeStamp)
    returnArr.push(date.getFullYear())
    returnArr.push(formatNumber(date.getMonth() + 1))
    returnArr.push(formatNumber(date.getDate()))

    returnArr.push(formatNumber(date.getHours()))
    returnArr.push(formatNumber(date.getMinutes()))
    returnArr.push(formatNumber(date.getSeconds()))

    for (var i in returnArr) {
      format = format.replace(formateArr[i], returnArr[i])
    }
    return format
  }
}
// permissionId enableWorkFlow
export const findMenuIdByPath = (leafId, nodes, resObj = {}) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = resObj
    tmpObj.menuId = nodes[i].permissionId
    tmpObj.enableWorkFlow = nodes[i].enableWorkFlow
    if (leafId === nodes[i].functionAddress) {
      return tmpObj
    }
    if (nodes[i].childPermissions) {
      const findResult = findMenuIdByPath(leafId, nodes[i].childPermissions, tmpObj)
      if (findResult) {
        return findResult
      }
    }
  }
}

export const findMenuInfoByPath = (leafId, nodes = [], resObj = {}) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = nodes[i]
    if (leafId === nodes[i].functionAddress) {
      return tmpObj
    }
    if (nodes[i].childPermissions) {
      const findResult = findMenuInfoByPath(leafId, nodes[i].childPermissions, tmpObj)
      if (findResult) {
        return findResult
      }
    }
  }
}

// 获取当前页面菜单信息
export const getPermissionInfo = () => {
  const menus = store.getters.userInfo.menus || {} // 当前用户下面的菜单
  let fnPath = window.location.hash || ''
  let curRouter = fnPath.replace(/#/g, '')// 当前路由路径
  return findMenuInfoByPath(curRouter, menus) || {}
}

// 新增单据标题公共方法
export const tabsAddFormTitle = i18n.t('common.newDocument') // '新增单据'

// 千位分隔符
export function toThousand (num, scale) {
  if (isNull(num)) {
    return ''
  }

  if (typeof num !== 'number') {
    num = parseFloat(num)
  }
  var reg = /\B(?=(\d{3})+$)/g
  num = num.toString().split('.')
  scale = scale === undefined ? 2 : scale

  num[0] = num[0].replace(reg, ',')
  num[1] = num[1] ? num[1].substr(0, scale) : '00000000000000000'.substr(0, scale)

  return scale ? num.join('.') : num[0]
}

/**
 * 转换数字类型
 * @param {*} val
 * @param {*} type
 * @param {*} defaultVal 为空时默认值
 */
export function toNumber (val, type = 'float', defaultVal) {
  if (isNull(val)) {
    return isNull(defaultVal) ? '' : defaultVal
  }

  let symbol = ''
  // 符号位判断
  if (val.toString().substring(0, 1) === '-') {
    symbol = '-'
  }

  // eslint-disable-next-line no-useless-escape
  val = val.toString().replace(/[^\d\.]/g, '')

  if (isNull(val)) {
    return isNull(defaultVal) ? '' : defaultVal
  }

  switch (type) {
  case 'float':
    val = parseFloat(val)
    break

  default:
    val = parseInt(val)
  }
  return parseFloat(val ? symbol + val : val)
}

/**
 * 动态加载脚本方法
 * @param {*} url 加载地址
 * @param {*} callback 回调函数
 */
export function loadJS (url, callback) {
  const script = document.createElement('script')
  const fn = callback || function () {}
  script.type = 'text/javascript'
  // IE
  if (script.readyState) {
    script.onreadystatechange = function () {
      if (script.readyState === 'loaded' || script.readyState === 'complete') {
        script.onreadystatechange = null
        fn()
      }
    }
  } else {
    // 其他浏览器
    script.onload = function () {
      fn()
    }
  }
  script.src = url
  document.getElementsByTagName('head')[0].appendChild(script)
}

/**
 * 表单校验定位到报错位置
 * **/
export function focusError () {
  this.$nextTick(() => {
    const isError = document.getElementsByClassName('is-error')
    if (isError[0].querySelector('input')) {
      isError[0].querySelector('input').scrollIntoView(true)
    }
    if (isError[0].querySelector('textarea')) {
      isError[0].querySelector('textarea').scrollIntoView(true)
    }
  })
}
/**
 * 页面定位到对应的区域
 * code 传入页面ref的命名值
 * type 标识原生标签还是element标签 原生标签用html标识 element UI 默认空
 * **/
export function jumpToTarget (code, type) {
  let anchorEle = null
  if (type && type === 'html') {
    // html原生标签
    anchorEle = this.$refs[code]
  } else {
    // element 组件
    anchorEle = this.$refs[code].$el
  }
  if (anchorEle) {
    anchorEle.scrollIntoView(true)
  }
}
// 渲染引擎-定位到报错位置并弹出错误信息
export function focusAndAlterErrorEngine (errList = []) {
  const firstKey = errList[0].messages[0]
  this.$message.error(typeof firstKey === 'object' ? firstKey.messages[0] : firstKey)
  // setTimeout(() => {
  //   const isError = document.getElementsByClassName('render-pix-form-item-error-help')
  //   if (isError[0].parentNode.querySelector('input')) {
  //     isError[0].parentNode.querySelector('input').scrollIntoView(true)
  //   }
  //   if (isError[0].parentNode.querySelector('textarea')) {
  //     isError[0].parentNode.querySelector('textarea').scrollIntoView(true)
  //   }
  // }, 50)
}

export function toThousandFilter (num) {
  return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}

// js数字计算丢失精度问题解决方案
export function operationNumber (arg1, arg2, operator) {
  var oper = ['+', '-', '*', '/']
  // 不合法的运算
  if (isNaN(arg1) || isNaN(arg2) || oper.indexOf(operator) < 0) {
    return NaN
  }
  // 除以0
  if (operator === '/' && Number(arg2) === 0) {
    return Infinity
  }
  // 和0相乘
  if (operator === '*' && Number(arg2) === 0) {
    return 0
  }
  // 相等两个数字相减
  if ((arg1 === arg2 || Number(arg1) === Number(arg2)) && operator === '-') {
    return 0
  }
  var r1, r2, max, _r1, _r2
  try {
    r1 = arg1.toString().split('.')[1].length
  } catch (e) {
    r1 = 0
  }
  try {
    r2 = arg2.toString().split('.')[1].length
  } catch (e) {
    r2 = 0
  }
  max = Math.max(r1, r2)
  _r1 = max - r1
  _r2 = max - r2
  if (_r1 !== 0) {
    arg1 = arg1 + '0'.repeat(_r1)
  }
  if (_r2 !== 0) {
    arg2 = arg2 + '0'.repeat(_r2)
  }
  arg1 = Number(arg1.toString().replace('.', ''))
  arg2 = Number(arg2.toString().replace('.', ''))
  var r3 = operator === '*' ? max * 2 : operator === '/' ? 0 : max
  // eslint-disable-next-line no-eval
  var newNum = eval(arg1 + operator + arg2)

  if (r3 !== 0) {
    var nStr = newNum.toString()
    nStr = nStr.replace(/^-/, '')
    if (nStr.length < r3 + 1) {
      nStr = '0'.repeat(r3 + 1 - nStr.length) + nStr
    }
    nStr = nStr.replace(new RegExp('(\\d{' + r3 + '})$'), '.$1')
    if (newNum < 0) {
      nStr = '-' + nStr
    }
    newNum = nStr * 1
  }
  return newNum
}

/**
 * 状态列显示不同状态对应不同的圆点颜色
 * 参数说明：
 * statusList 状态的颜色数组
 * value 当前值
 */
export const getStatusClass = (statusList, value) => {
  let green = statusList.green || greenStatus
  let red = statusList.red || redStatus
  let orange = statusList.orange || orangeStatus
  let invalid = statusList.invalid || invalidStatus
  let style = 'status-tag'
  if (green.includes(value)) {
    return 'status-tag green'
  }
  if (red.includes(value)) {
    return 'status-tag red'
  }
  if (orange.includes(value)) {
    return 'status-tag orange'
  }
  if (invalid.includes(value)) {
    return 'status-tag invalid'
  }
  return style
}

/**
 * 供应商名称脱敏
 * 参数说明：
 * str: 传入供应商名称
 * beginLen：前面需要显示几个字符
 * endLen：后面需要显示几个字符
 */
export const desensitizedVendor = (str, beginLen = 1, endLen = 1) => {
  if (str) {
    str = str.toString()
    if (beginLen < 0) beginLen = 0
    if (endLen < 0) endLen = 0
    if (str.length <= beginLen + endLen) {
      if (beginLen < str.length) {
        endLen = 0
      } else {
        beginLen = endLen = 0
      }
    }
    let reg = new RegExp(`^(.{${beginLen}})(?:[^]+)(.{${endLen}})$`)
    let result = str.replace(reg, '$1***$2')
    return result
  } else {
    return ''
  }
}
/**
 * 获取对象里面key和value值
 */
export const getObjectKeyValue = (obj = {}) => {
  let str = ''
  Object.keys(obj).forEach(keys => {
    str += keys + '字段信息提示：' + obj[keys] + ';'
  })
  return str
}

/**
 * 节流函数  用于防止表单提交按钮被多次触发
 * @param  fn  function
 * @param gapTime  节流间隙时间
 */
export const throttle = (fn, gapTime = 250) => {
  let lastTime = null
  return function () {
    let nowTime = new Date()
    let context = this
    let args = arguments
    if (nowTime - lastTime > gapTime || !lastTime) {
      fn.apply(context, args)
      lastTime = nowTime
    }
  }
}

/**
 * 处理列表中多表关联查询条件
 * @param  query  查询条件
 * @param condition  需要处理的联表字段
 */
export const transformQuery = (query, condition) => {
  if (condition.length) {
    condition.forEach(item => {
      let list = item.split('.')
      list.reduce(function (prev, cur, index, arr) {
        let obj = prev ? prev[cur] : query[cur]
        if (obj?.$condition) {
          obj.$condition.$strictQuery = true
        } else {
          obj.$condition = { $strictQuery: true }
        }
        return obj
      }, null)
    })
  }
  return query
}

/**
 * 处理详情中多表关联查询条件
 * @param  query  查询条件
 * @param condition  需要处理的联表字段
 */
export const transformDetailQuery = (query, condition) => {
  if (condition.length) {
    condition.forEach(item => {
      let list = item.split('.')
      list.reduce(function (prev, cur, index, arr) {
        let obj = prev ? prev[cur] : query[cur]
        if (!obj['*']) {
          obj['*'] = {}
        }
        return obj
      }, null)
    })
  }
  return query
}

/**
 * 批量操作列表时筛选出不符合条件的行序号
 * @param  rows  批量操作行数据
 * @param field  行序号字段
 * @param fn  过滤函数
 */
export const getValidateFailureSequence = (rows, field, fn) => {
  if (!rows.length) return
  if (typeof fn !== 'function') {
    throw new TypeError('Expected a function')
  }
  return rows
    .filter(fn)
    .map(i => i[field])
    .join()
}

/**
 * 函数柯里化
 * @param  fn  需要柯里化的函数
 */
export const currying = fn => {
  if (typeof fn !== 'function') {
    throw new TypeError('Expected a function')
  }
  let len = fn.length
  return function (...args) {
    return args.length >= len ? fn.apply(this, args) : currying(fn.bind(this, ...args))
  }
}

/**
 * 处理列表中合并展示的数据，满足导出组件的参数要求
 * @param  columns  列表展示列
 * @param  conditions  导出组件实际需要的列，示例 [{targetFiled:'orderNumberAndLineNum',filed:'orderNumber',title:'订单号'},{targetFiled:'orderNumberAndLineNum',filed:'lineNum',title:'订单行号'}]
 */
export const transformColumns = (columns, conditions) => {
  columns.forEach((item, index) => {
    let i = index
    conditions.forEach(innerItem => {
      if (item.field === innerItem.targetFiled) {
        columns.splice(i, index === i ? 1 : 0, { field: innerItem.field, title: innerItem.title })
        i++
      }
    })
  })
  return columns
}

/**
 * 处理详情中多表关联查询的返回数据
 * @param  detailListItem  返回字段detailList的元素
 * @param condition  需要处理的联表字段 originalData.ref
 * @param condition  需要处理的联表字段
 * @param type  供应商 'Buyer'  采购商 'Vendor', 非必选
 */
export const transformDetailDetailListItem = (detailListItem, originalDataRef, condition, type = '') => {
  let obj = {}
  if (condition.length) {
    condition.forEach(conditionItem => {
      let list = conditionItem.split('.')
      list.reduce(function (prev, cur, index, arr) {
        let str = cur
        str = str.replace(str[0], str[0].toLowerCase())
        str = str.indexOf(type) ? str.slice(0, str.length - type.length) : str
        let id = prev ? prev[str + 'Id'] : detailListItem[str + 'Id']
        let data = originalDataRef[cur]
        obj[str + 'Item'] = data[id]
        return obj[str + 'Item']
      }, null)
    })
  }
  return obj
}

// 获取对象里面非数组的 主要用在m解析meiql的结构
export const getHeaderField = (Obj = {}) => {
  let nonArrayFields = {}
  for (let key in Obj) {
    let value = Obj[key]
    if (!Array.isArray(value) || (typeof value !== 'object')) {
      nonArrayFields[key] = value
    }
  }
  return nonArrayFields
}
