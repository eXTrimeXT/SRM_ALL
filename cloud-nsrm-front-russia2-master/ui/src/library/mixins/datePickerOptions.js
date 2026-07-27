/**
 * 时间组件相关配置
 */

/* 不能选择小于当天时间 */
export const cannotLessCurrentTime = {
  data () {
    return {
      cannotLessCurrentTimeOptions: {
        disabledDate: time => {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return time.getTime() < nowDate.getTime()
        }
      }
    }
  }
}

/* 不能选择大于当天时间 */
export const cannotLargerCurrentTime = {
  data () {
    return {
      cannotLargerCurrentTimeOptions: {
        disabledDate: time => {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return time.getTime() > nowDate.getTime()
        }
      }
    }
  }
}
