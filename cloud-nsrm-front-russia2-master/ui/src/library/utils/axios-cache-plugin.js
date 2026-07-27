class Cacher {
  constructor (option) {
    this.cacheMap = new Map()
    this.option = option || {}
    this.maxCacheSize = this.option.maxCacheSize || 15
    this.ttl = this.option.ttl
  }

  /**
   * [setCache 添加缓存]
   * @param {[any]} key
   * @param {[any]} value
   */
  setCache (key, value) {
    this.cacheMap.set(JSON.stringify(key), value)
    if (this.maxCacheSize && this.cacheMap.size > this.maxCacheSize) {
      this.cacheMap.delete([...this.cacheMap.keys()][0])
    }
    if (this.ttl) {
      setTimeout(() => {
        if (this.hasCache(key)) {
          this.cacheMap.delete(JSON.stringify(key))
        }
      }, this.ttl)
    }
  }

  /**
   * [hasCache 是否已有缓存]
   * @param  {[any]}  key
   * @return {Boolean}
   */
  hasCache (key) {
    return this.cacheMap.has(JSON.stringify(key))
  }

  /**
   * [getCache 获取缓存内容]
   * @param  {[any]} key
   * @return {[any]}
   */
  getCache (key) {
    return this.cacheMap.get(JSON.stringify(key))
  }

  /**
   * [clear 清空缓存]
   */
  clear () {
    this.cacheMap.clear()
  }
}

/**
 * [wrapper 包装器]
 * @param  {[axios instance]} instance
 * @param  {[obj]} option
 * @return {[axios instance with cache feature]}
 */
export default function wrapper (instance, option) {
  const cacher = new Cacher(option)

  const unCacheMethods = ['delete', 'head', 'options', 'put', 'patch']

  /**
   * [axiosWithCache axios instance Proxy]
   * @param  {...[any]} arg
   * @return {[promise]}
   */
  function axiosWithCache (...arg) {
    console.log('[arg]', arg)
    if (
      arg.length === 1 &&
      ['GET', undefined, 'POST'].includes(arg[0].method)
    ) {
      return requestWithCacheCheck(arg[0], instance, ...arg)
    } else {
      return instance(...arg)
    }
  }

  /**
   * [requestWithCacheCheck 请求检查缓存，返回结果 promise]
   * @param  {[obj]}    option
   * @param  {[request handler func]}    func
   * @param  {...[any]} arg
   * @return {[promise]}
   */
  function requestWithCacheCheck (option, func, ...arg) {
    if (cacher.hasCache(option)) {
      return Promise.resolve({
        __fromAxiosCache: true,
        ...cacher.getCache(option)
      })
    } else {
      return func(...arg).then(response => {
        cacher.setCache(option, response)
        return response
      })
    }
  }

  /**
   * [get axios instance get function proxy]
   * @param  {...[any]} arg
   * @return {[promise]}
   */
  axiosWithCache.get = function (...arg) {
    console.log(arg)
    if (arg.length === 1) {
      return requestWithCacheCheck(
        {
          url: arg[0]
        },
        instance.get,
        ...arg
      )
    } else if (arg.length === 2) {
      return requestWithCacheCheck(
        {
          url: arg[0],
          ...arg[1]
        },
        instance.get,
        ...arg
      )
    } else {
      return instance.get(...arg)
    }
  }

  /**
   * [cacher instance proxy]
   */
  axiosWithCache.__cacher = cacher

  /**
   * [__clearCache cacher instance clear function proxy]
   */
  axiosWithCache.__clearCache = function () {
    cacher.clear()
  }

  /**
   * [proxy axios instance functions which are no need to be cached]
   */
  unCacheMethods.forEach(method => {
    axiosWithCache[method] = function (...arg) {
      return instance[method](...arg)
    }
  })

  return axiosWithCache
}
