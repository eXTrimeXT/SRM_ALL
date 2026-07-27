import http from '@/utils/axios/http'
import { messageConfig } from '@/utils/message'
import { Message } from '@meicloud/element-ui' // 'element-ui'
import { validatenull } from './validate'

/**
 * 传统vue查询参数转换成key-object格式传至后端
 */
export const transformQueryParam = (params, filterConfig) => {
  let paramsData = {
    'payload': {
      'filter': {},
      'page': {
        'sort': 'lastUpdateDate desc'
      }

    }
  }
  if (Object.keys(params).length <= 0) { // 没有查询条件的时候
    return paramsData
  } else { // 有查询条件的时候
    let filter = {}
    for (let key in params) {
      let filterItem = filterConfig.find(item => item.prop === key)
      // 需要设置queryOperator属性，同x-query-engine-query-operator
      if (filterItem.queryOperator) {
        filter[key] = { [filterItem.queryOperator]: params[key] }
      } else {
        // 默认是eq
        filter[key] = { eq: params[key] }
      }
    }
    paramsData.payload.filter = filter
    return paramsData
  }
}

/**
 * 传统vue转换成MQL数据传至后端
 */
export const transformMQL = {
  /**
   * 保存时用来转化数据成MQL使用的数据
   * type:是定义的哪一张表
   * action:哪个action 默认是save
   * payload:数据
   * query:保存到哪些表
   */
  save: (type, payload, action, query) => {
    return {
      type: type ?? null,
      action: action ?? 'save',
      payload: payload ?? [],
      lang: 'zh-cn',
      query: !query ? { '*': {} } : query,
      tree: true
    }
  },
  /**
   * 列表页输入type与params
   * type:是定义的哪一张表
   * params:点击查询后传过来查询的值
   * query:保存到哪些表
   * sort:以哪个字段作为排序,默认是lastUpdateDate
   * action:哪个action 默认是query
   * filter:特殊的案例需要自己编写过滤条件，例如日期筛选大于等于等,如无特殊的可以直接不传
   * filterOperator:类似 x-query-engine-query-operator 配置分割符 eg { name:'eq' }
   */
  listGetData: (type = null, params = {}, sort = 'lastUpdateDate', query = { '*': {} }, action = 'query', filter = null, filterOperator = {}) => {
    let paramsData = {
      type,
      lang: 'zh-cn',
      query: query,
      payload: {
        filter: {},
        page: {
          sort: sort + ' desc'
        }
      },
      action,
      tree: true
    }
    if (Object.keys(params).length <= 0) { // 没有查询条件的时候
      return paramsData
    } else { // 有查询条件的时候
      if (filter) {
        paramsData.payload.filter = filter
        return paramsData
      } else {
        let filter = {}
        for (let key in params) {
          if (params[key] || params[key] === 0) {
            if (filterOperator[key]) {
              filter[key] = { [filterOperator[key]]: params[key] }
            } else {
              filter[key] = { contains: params[key].toString() } // contains 后端定义只能传字符串
            }
          }
        }
        paramsData.payload.filter = filter
        return paramsData
      }
    }
  },
  /**
   * 列表页输入type与params
   * type:是定义的哪一张表
   * params:点击查询后传过来查询的值
   * query:保存到哪些表
   * sort:以哪个字段作为排序,默认是lastUpdateDate
   * action:哪个action 默认是query
   * filter:特殊的案例需要自己编写过滤条件，例如日期筛选大于等于等,如无特殊的可以直接不传
   * filterOperator:类似 x-query-engine-query-operator 配置分割符 eg { name:'eq' }
   */
  listPageData: ({
    type = null,
    params = {},
    sort = 'lastUpdateDate',
    query = { '*': {} },
    action = 'query',
    filter = null,
    filterOperator = {},
    pageNum, // tableView组件不用传，组件里面有传
    pageSize,
    payloadFlag = 'Y'
  }) => {
    let paramsData = {
      type,
      lang: 'zh-cn',
      query: query,
      payload: {
        filter: {},
        page: {
          sort: sort + ' desc',
          pageNum,
          pageSize
        }
      },
      action,
      tree: true
    }
    if (!filter) {
      filter = {}
      for (let key in params) {
        if (params[key] || params[key] === 0) {
          if (filterOperator[key]) {
            filter[key] = { [filterOperator[key]]: params[key] }
          } else {
            filter[key] = { contains: params[key].toString() } // contains 后端定义只能传字符串
          }
        }
      }
    }
    paramsData.payload.filter = payloadFlag === 'Y' ? filter : params
    return paramsData
  }

}

// 表单序列化
export const serialize = data => {
  let list = []
  Object.keys(data).forEach(ele => {
    list.push(`${ele}=${data[ele]}`)
  })
  return list.join('&')
}
export const getObjType = obj => {
  var toString = Object.prototype.toString
  var map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  }
  if (obj instanceof Element) {
    return 'element'
  }
  return map[toString.call(obj)]
}
/**
 * 对象深拷贝
 */
export const deepClone = data => {
  var type = getObjType(data)
  var obj
  if (type === 'array') {
    obj = []
  } else if (type === 'object') {
    obj = {}
  } else {
    // 不再具有下一层次
    return data
  }
  if (type === 'array') {
    for (var i = 0, len = data.length; i < len; i++) {
      obj.push(deepClone(data[i]))
    }
  } else if (type === 'object') {
    for (var key in data) {
      obj[key] = deepClone(data[key])
    }
  }
  return obj
}
/**
 * 判断路由是否相等
 */
export const diff = (obj1, obj2) => {
  delete obj1.close
  var o1 = obj1 instanceof Object
  var o2 = obj2 instanceof Object
  if (!o1 || !o2) { /*  判断不是对象  */
    return obj1 === obj2
  }

  if (Object.keys(obj1).length !== Object.keys(obj2).length) {
    return false
    // Object.keys() 返回一个由对象的自身可枚举属性(key值)组成的数组,例如：数组返回下表：let arr = ['a', 'b', 'c'];console.log(Object.keys(arr))->0,1,2;
  }

  for (var attr in obj1) {
    var t1 = obj1[attr] instanceof Object
    var t2 = obj2[attr] instanceof Object
    if (t1 && t2) {
      return diff(obj1[attr], obj2[attr])
    } else if (obj1[attr] !== obj2[attr]) {
      return false
    }
  }
  return true
}
/**
 * 设置灰度模式
 */
export const toggleGrayMode = (status) => {
  if (status) {
    document.body.className = document.body.className + ' grayMode'
  } else {
    document.body.className = document.body.className.replace(' grayMode', '')
  }
}
/**
 * 设置主题
 */
export const setTheme = (name) => {
  document.body.className = name
}

/**
 *加密处理
 */
export const encryption = (params) => {
  let {
    data,
    type,
    param,
    key
  } = params
  const result = JSON.parse(JSON.stringify(data))
  if (type === 'Base64') {
    param.forEach(ele => {
      result[ele] = btoa(result[ele])
    })
  } else {
    param.forEach(ele => {
      var data = result[ele]
      // eslint-disable-next-line no-undef
      key = CryptoJS.enc.Latin1.parse(key)
      var iv = key
      // 加密
      // eslint-disable-next-line no-undef
      var encrypted = CryptoJS.AES.encrypt(
        data,
        key, {
          iv: iv,
          // eslint-disable-next-line no-undef
          mode: CryptoJS.mode.CBC,
          // eslint-disable-next-line no-undef
          padding: CryptoJS.pad.ZeroPadding
        })
      result[ele] = encrypted.toString()
    })
  }
  return result
}

/**
 * 浏览器判断是否全屏
 */
export const fullscreenToggel = () => {
  if (fullscreenEnable()) {
    exitFullScreen()
  } else {
    reqFullScreen()
  }
}
/**
 * esc监听全屏
 */
export const listenfullscreen = (callback) => {
  function listen () {
    callback()
  }

  document.addEventListener('fullscreenchange', function () {
    listen()
  })
  document.addEventListener('mozfullscreenchange', function () {
    listen()
  })
  document.addEventListener('webkitfullscreenchange', function () {
    listen()
  })
  document.addEventListener('msfullscreenchange', function () {
    listen()
  })
}
/**
 * 浏览器判断是否全屏
 */
export const fullscreenEnable = () => {
  return document.isFullScreen || document.mozIsFullScreen || document.webkitIsFullScreen
}

/**
 * 浏览器全屏
 */
export const reqFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.documentElement.requestFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.documentElement.webkitRequestFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.documentElement.mozRequestFullScreen()
  }
}
/**
 * 浏览器退出全屏
 */
export const exitFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.exitFullScreen()
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.webkitCancelFullScreen()
  } else if (document.documentElement.mozRequestFullScreen) {
    document.mozCancelFullScreen()
  }
}
/**
 * 递归寻找子类的父类
 */

export const findParent = (menu, id) => {
  for (let i = 0; i < menu.length; i++) {
    if (menu[i].children.length !== 0) {
      for (let j = 0; j < menu[i].children.length; j++) {
        if (menu[i].children[j].id === id) {
          return menu[i]
        } else {
          if (menu[i].children[j].children.length !== 0) {
            return findParent(menu[i].children[j].children, id)
          }
        }
      }
    }
  }
}

/**
 * 动态插入css
 */

export const loadStyle = url => {
  const link = document.createElement('link')
  link.type = 'text/css'
  link.rel = 'stylesheet'
  link.href = url
  const head = document.getElementsByTagName('head')[0]
  head.appendChild(link)
}
/**
 * 判断路由是否相等
 */
export const isObjectValueEqual = (a, b) => {
  let result = true
  Object.keys(a).forEach(ele => {
    const type = typeof (a[ele])
    if (type === 'string' && a[ele] !== b[ele]) result = false
    else if (type === 'object' && JSON.stringify(a[ele]) !== JSON.stringify(b[ele])) result = false
  })
  return result
}

/**
 * 根据字典的 dictItemCode 显示 dictItemName
 */
export const findByvalue = (dic, value) => {
  let result = ''
  if (validatenull(dic)) return value
  if (typeof (value) === 'string' || typeof (value) === 'number' || typeof (value) === 'boolean') {
    let index = 0
    index = findArray(dic, value)
    if (index !== -1) {
      result = dic[index].dictItemName
    } else {
      result = value
    }
  } else if (value instanceof Array) {
    result = []
    let index = 0
    value.forEach(ele => {
      index = findArray(dic, ele)
      if (index !== -1) {
        result.push(dic[index].dictItemName)
      } else {
        result.push(value)
      }
    })
    result = result.toString()
  }
  return result
}
/**
 * 根据字典的value查找对应的index
 */
export const findArray = (dic, value) => {
  for (let i = 0; i < dic.length; i++) {
    if (dic[i].value === value) {
      return i
    }
  }
  return -1
}
/**
 * 生成随机len位数字
 */
export const randomLenNum = (len, date) => {
  let random = ''
  // random = Math.ceil(Math.random() * 100000000000000).toString().substr(0, len || 4)
  random = Math.ceil(window.crypto.getRandomValues(new Uint8Array(1)) * 0.001 * 100000000000000).toString().substr(0, len || 4)
  if (date) random = random + Date.now()
  return random
}
/**
 * 打开小窗口
 */
export const openWindow = (url, title, w, h) => {
  // Fixes dual-screen position                            Most browsers       Firefox
  const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left
  const dualScreenTop = window.screenTop !== undefined ? window.screenTop : screen.top

  const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width
  const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height

  const left = ((width / 2) - (w / 2)) + dualScreenLeft
  const top = ((height / 2) - (h / 2)) + dualScreenTop
  const newWindow = window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, copyhistory=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left)
  newWindow.opener = null
  // Puts focus on the newWindow
  if (window.focus) {
    newWindow.focus()
  }
}

/**
 *  <img> <a> src 处理
 * @returns {PromiseLike<T | never> | Promise<T | never>}
 */
export function handleImg (fileName, id) {
  return validatenull(fileName) ? null : http({
    url: '/admin/file/' + fileName,
    method: 'get',
    responseType: 'blob'
  }).then((response) => { // 处理返回的文件流
    let blob = response.data
    let img = document.getElementById(id)
    img.src = URL.createObjectURL(blob)
    window.setTimeout(function () {
      window.URL.revokeObjectURL(blob)
    }, 0)
  })
}

export const filterForm = (form) => {
  let obj = {}
  Object.keys(form).forEach(ele => {
    if (!validatenull(form[ele])) {
      obj[ele] = form[ele]
    }
  })
  return obj
}

export const vaildData = (val, dafult) => {
  if (typeof val === 'boolean') {
    return val
  }
  return !validatenull(val) ? val : dafult
}

export const throttle = (fn, interval = 300) => {
  // eslint-disable-next-line no-unused-expressions
  let canRun = true
  return function () {
    if (!canRun) return
    canRun = false
    setTimeout(() => {
      fn.apply(this, arguments)
      canRun = true
    }, interval)
  }
}

// 获取url 参数
export const getUrlKey = (name) => {
  // eslint-disable-next-line no-sparse-arrays
  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
}

// 删除url中某个参数,并跳转
export const funcUrlDel = (paramKey) => {
  var url = window.location.href // 页面url
  var urlParam = window.location.hash.substr(1) // 页面参数
  var beforeUrl = url.substr(0, url.indexOf('?')) // 页面主地址（参数之前地址）
  var nextUrl = ''
  var arr = []
  if (urlParam.indexOf(paramKey) > -1) {
    let searParamArr = urlParam.substr(urlParam.indexOf('?') + 1)
    var urlParamArr = searParamArr.split('&') // 将参数按照&符分成数组
    for (var i = 0; i < urlParamArr.length; i++) {
      var paramArr = urlParamArr[i].split('=') // 将参数键，值拆开
      // 如果键雨要删除的不一致，则加入到参数中
      if (paramArr[0] !== paramKey) {
        arr.push(urlParamArr[i])
      }
    }
  }
  if (arr.length > 0) {
    nextUrl = '?' + arr.join('&')
  }
  url = beforeUrl + nextUrl
  return url
}

/**
* @Description: 添加明细去重
* @param data  表格数据 (必填)
* @param selection 选中值 (必填)
* @param condition 筛选条件 (必填),允许自己写回调组合条件筛选
* @param lineSet 行回调更新添加值 (选填)
*/
export const setRepeatData = (data, selection, condition, lineSet) => {
  let getCondition = (row) => typeof condition === 'function' ? condition(row) : row[condition]

  let dataArr = []
  let isTip = false
  let ids = data.map(item => getCondition(item))

  for (let row of selection) {
    if (ids.includes(getCondition(row))) {
      isTip = true
    } else {
      let otherFiled = lineSet ? lineSet(row) : {}
      dataArr.push({ ...row, ...otherFiled })
    }
  }
  if (isTip) {
    Message({
      ...messageConfig,
      message: '已剔除重复勾选数据，明细行应唯一！',
      type: 'warning'
    })
  }
  data.push(...dataArr)
  return data
}

/**
 * 校验表单表格必填项
 * @param refs ref挂载数组
 * @return flag 判断校验是否通过标识
 * @return message 校验不通过时抛出必填项message报错信息
 */
export const getCheckForm = async (refs) => {
  // 设置校验返回值
  const setValidateMessage = (fileds) => {
    for (let filed of fileds) {
      if (!filed.flag && Object.keys(filed.obj).length > 0) {
        const warnObj = Object.keys(filed.obj)[0]
        return {
          flag: filed.flag,
          message: filed.obj[warnObj][0].message
        }
      }
    }
    return { flag: true }
  }
  // 校验加套promise返回trun or false
  const formValidate = (formRef) => {
    return new Promise((resolve) => {
      formRef.validate((flag, obj) => {
        resolve({ flag, obj })
      })
    })
  }

  const checkArr = refs.map(ref => formValidate(ref))
  let validateArr = await Promise.all(checkArr)

  return setValidateMessage(validateArr)
}

/**
 * MQL校验表单表格具体信息报错提示
 * @params err 调用$form.validate().catch((err) => setWarningTip(err))
 * @params $message MQL传入Message实例
 */
export const setWarningTip = (err) => {
  let address = err[0]?.address

  // 表格情况下
  if (address.match(/\d/)) {
    const index = address.match(/\d/)[0] + 1
    return Message({
      ...messageConfig,
      message: '明细第' + Number(index) + '行' + err[0].messages[0],
      type: 'warning'
    })
  } else {
    return Message({
      ...messageConfig,
      message: err[0].messages[0],
      type: 'warning'
    })
  }
}

/**
 * @description 获取明细关联表还有第二层级关联情况数组融合，如果第二层级之后任然有关联关系，请继续按规则调用此方法（再深层级未实践）
 * @param ref 渲染引擎success返回值：res.originalData.ref
 * @param list 需要操作的明细数据（需要遍历融合的目标对象）
 * @param levelGroup 第一层级关联关系
 * @param secondLevelGroup 第二层级关联关系
 * @return 返回两张表数据融合的新数组
 */
export const getRelatedData = (ref = {}, list = [], levelGroup = [], secondLevelGroup = []) => {
  // 第一层级关联明细和id
  const [levelA, listId] = levelGroup
  // 第二层级关联明细和id
  const [levelB, levelAId] = secondLevelGroup

  // 拿到明细表/关联表
  const { [levelA]: levelAData, [levelB]: levelBData } = ref

  // 返回两张表数据融合的新数组
  return list.map((row) => {
    // 获取行关联key
    const idKey = row[listId]
    // 获取关联表关联key
    const { [levelAId]: levelAIdKey } = levelAData[idKey]
    // 获取目标数据
    return { ...levelAData[idKey], ...levelBData[levelAIdKey] }
  })
}

/**
 * 渲染引擎列表查询过滤状态方法
 * @param filter 即传入data.payload.filter
 * @param status 状态字段
 * @param init 初始显示状态数组
 * @return filter 新filter
 */
export const filterStatusInit = (filter, status, init) => {
  if (!filter) filter = {}
  return {
    ...filter,
    [status]: filter[status] ? filter[status] : { in: init }
  }
}

/**
 * @param {string/number} value 处理字符串或者数值型
 * @param {number} limit 添加限制位数 默认限制8位
 * @return {number} 返回处理后的数值型数据
 */
export const processNumericValue = (value, limit = 8) => {
  // 转换为数值类型
  const numericValue = Number(value)
  // 检查是否是数值型
  if (isNaN(numericValue)) {
    return value
  }
  // 限制最多limit位小数
  const roundedValue = numericValue.toFixed(limit)
  // 删除末尾的0
  const trimmedValue = roundedValue.replace(/\.?0+$/, '')
  return trimmedValue
}

/**
 *
 * @param {*} json 待处理的字符串，多层嵌套
 * @returns 解析过后的对象
 * eg: "\"\\\"[\\\\\\\"410000\\\\\\\",\\\\\\\"410300\\\\\\\"]\\\"\"" -> ['410000', '410300']
 */
const parseJson = (json) => {
  if (typeof json === 'string') {
    json = JSON.parse(json)
    return parseJson(json)
  } else {
    return json
  }
}

/**
 *
 * @param {*} field  creationDate 多个日期
 * @param {*} obj getQueryData 对应的params
 */
export const transformTimeQuery = (field, obj) => {
  for (let item of field) {
    const { [item]: key } = obj
    if (key && key.length) {
      key[0] = key[0] + ' 00:00:00'
      key[1] = key[1] + ' 23:59:59'
    }
  }
}
