import http from '@/utils/axios/http' // http

function logger (target, name, descriptor) {
  let asyncFunction = descriptor.value
  descriptor.value = function (...args) {
    let result
    try {
      result = asyncFunction.apply(this, args)
      result
        .then(rs => {
          console.log('logger success:', rs)
          if (!rs.businessId) return false
          http({
            url: '/api-base/businessInfoLog/save',
            method: 'POST',
            data: rs,
            loading: false
          }).then(data => {
            console.log('logger axios data:', data)
          })
          // 成功，调用接口上传操作日志
        })
        .catch(rj => {
          console.error('logger error:', rj)
          // 失败
        })
    } catch (error) {
      console.error('logger error:', error)
      // 异常
    }
    return result
  }

  return descriptor
}

export default {
  install (Vue) {
    if (Vue.logger || window.logger) {
      return
    }

    Vue.logger = logger
    window.logger = logger
    Object.defineProperties(Vue.prototype, {
      logger: {
        get () {
          return logger
        }
      }
    })
  }
}
