import http from '@/utils/axios/http'
const getUrl = path => `${path}`

const promiseCache = new Map()

function getComponentInfo (elemCode) {
  const key = `code@${elemCode}`
  let promise = promiseCache.get(key)
  // 当前promise缓存中没有 该promise
  if (!promise) {
    promise = http({
      url: getUrl('/api-cm/elem-maintain/listPage'),
      method: 'POST',
      data: { elemCode },
      loading: true
    }).catch(error => {
      // 在请求回来后，如果出现问题，把promise从cache中删除 以避免第二次请求继续出错
      promiseCache.delete(key)
      return Promise.reject(error)
    })
    promiseCache.set(key, promise)
  }
  // 返回promise
  return promise
}

export default getComponentInfo
