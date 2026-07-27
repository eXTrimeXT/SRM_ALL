/**
 * 异步请求防抖，通常用于提交单据请求
 */

const requestingMap = new Map()

// 判断相同URL请求和参数是否进行中，如果不存在就加入
const stopRepeatRequest = (url, data, cancel, debounceMessage = '请勿重复请求') => {
  const dataString = JSON.stringify(data)
  const mapObj = requestingMap.get(url)
  if (mapObj && mapObj === dataString) {
    // 已存在，触发防抖 消息体添加防抖标识，报错用于识别
    cancel('debounce-' + debounceMessage)
  } else {
    // 不存在 会覆盖原来相同的key 值
    requestingMap.set(url, dataString)
  }
}

// 根据URL移除请求正在进行中的请求
const allowRequestUrl = url => {
  requestingMap.delete(url)
}

export {
  stopRepeatRequest,
  allowRequestUrl
}
