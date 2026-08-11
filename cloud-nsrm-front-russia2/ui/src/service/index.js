/**
 * 挂载API接口
 */

const apiContext = import.meta.glob('./modules/*.js', { eager: true })
const api = {}
Object.keys(apiContext).forEach(key => {
  const apiName = key
    .split('/')
    .pop()
    .replace(/\.\w+$/, '')
  api[apiName] = apiContext[key]
})

export default {
  install (Vue) {
    Vue.prototype.$api = api
  }
}
