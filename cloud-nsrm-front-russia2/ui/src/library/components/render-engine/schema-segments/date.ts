/**
 * 时间组件相关配置
 */
import { generateCharFunctionExpression } from '@meicloud/render-engine'

/**
 * 不能选择小于当天时间 注意要注入$dayjs
 */
const cannotLessCurrentTimeSegment = {
  pickerOptions: {
    disabledDate: generateCharFunctionExpression(({ $dayjs }, time) => {
      const [nowDate, valueDate] = [
        $dayjs().hour(0).minute(0).second(0).unix(),
        $dayjs(time).unix()
      ]
      return valueDate < nowDate
    })
  }
}

/**
 * 不能选择大于当天时间 注意要注入$dayjs
 */
const cannotLargerCurrentTimeSegment = {
  pickerOptions: {
    disabledDate: generateCharFunctionExpression(({ $dayjs }, time) => {
      const [nowDate, valueDate] = [
        $dayjs().hour(0).minute(0).second(0).unix(),
        $dayjs(time).unix()
      ]
      return valueDate > nowDate
    })
  }
}

export {
  cannotLessCurrentTimeSegment,
  cannotLargerCurrentTimeSegment
}
