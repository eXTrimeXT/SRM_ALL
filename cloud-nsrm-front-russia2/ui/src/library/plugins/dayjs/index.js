import dayjs from 'dayjs'
import weekday from 'dayjs/plugin/weekday'

/**
 * @description 利用dayjs格式化时间，严格判断值是否合法，不合法返回自定义空值
 * @author donghf3
 * @param value 值
 * @param format 格式，默认年-月-日
 * @param empty 空值
 * @returns {string|*}
 */
const dayjsParse = (value, format = 'YYYY-MM-DD', empty = '') => {
  if (value && dayjs(value, format, true).isValid()) {
    // 检查合法
    return dayjs(value).format(format)
  }
  // 不合法返回空
  return empty
}

export default {
  install (Vue) {
    dayjs.extend(weekday)
    Vue.prototype.$dayjs = dayjs
    Vue.prototype.$dayjsParse = dayjsParse
  }
}
