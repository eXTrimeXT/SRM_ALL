import { Message } from '@meicloud/element-ui'
import { isString, isObject } from 'lodash'
import Vue from 'vue'

// 弹窗属性配置 外部可直接调用
export const messageConfig = {
  showClose: true
}

const innerMessage = (option, config = {}) => {
  return Message({
    ...(
      isObject(option) ? option : { message: option }
    ),
    ...messageConfig,
    ...config
  })
}

/**
 * @description 全局消息属性设置 配置全局属性添加至config
 */
export const setGlobalMessage = () => {
  Vue.prototype.$message = function (option, config) {
    return innerMessage(option, config)
  }

  const messageType = ['info', 'success', 'warning', 'error']
  messageType.forEach((type) => {
    Vue.prototype.$message[type] = function (options, config = {}) {
      return innerMessage(options, { type, ...config })
    }
  })
}
