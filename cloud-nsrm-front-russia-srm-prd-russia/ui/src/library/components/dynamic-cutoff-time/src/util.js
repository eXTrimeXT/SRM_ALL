import { isInteger } from 'lodash'
import i18n from '@/lang'
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
// 加载duration插件
dayjs.extend(duration)

/**
 *
 * 传入毫秒，返回毫秒转换的年月天时分秒
 * @param millisecond 毫秒
 * @param unit 根据配置返回转换值，比如配置days，只返回天，hours只返回毫秒转的小时数以此类推
 * @returns {{hours: number, seconds: number, months: number, minutes: number, days: number, years: number}|null|{}}
 */
export const parseTimeByMillisecond = (millisecond, unit = '') => {
  if (!isInteger(millisecond) || millisecond <= 0) {
    return null
  }
  const methodsEnum = {
    // 年
    years: ['years', 'asYears'],
    // 月
    months: ['months', 'asMonths'],
    // 天
    days: ['days', 'asDays'],
    // 小时
    hours: ['hours', 'asHours'],
    // 分钟
    minutes: ['minutes', 'asMinutes'],
    // 秒
    seconds: ['seconds', 'asSeconds']
  }

  let parseTime = dayjs.duration(millisecond)

  let timeValue = {}
  if (unit && methodsEnum[unit]) {
    // 只返回一个
    timeValue = {
      ...timeValue,
      // 默认保留两位小数
      [unit]: parseTime[methodsEnum[unit][1]]().toFixed(2)
    }
  } else {
    Object.keys(methodsEnum).forEach(item => {
      timeValue = {
        ...timeValue,
        [item]: parseTime[methodsEnum[item][0]]()
      }
    })
  }
  return timeValue
}

/**
 * 计算两个时间的时间差，并格式化成字符串返回，第一个时间需要比第二个时间大。支持剩余时间计算
 * @param {string | number} time1 对比时间1 可被dayjs格式化
 * @param {string | number | null} time2 对比时间2 可被dayjs格式化 若与当前时间对比传空
 * @param {object} params { deadlineText: '', unit: '' }
 * @param {boolean} isRemainingTime 是否是剩余时间，time1传参为毫秒
 * @param {string} deadlineText 截止文本 默认返回空
 * @param {string} unit 根据配置返回转换值，比如配置days，只返回天，hours只返回毫秒转的小时数以此类推
 * @param {object} $i18n 自定义国际化 如果不传则使用工程默认插件
 * @returns {{value: string, status: boolean}|{status: boolean}}
 */
export const dynamicCutoffTimeDiffToString = (time1, time2 = '', params = {}) => {
  const {
    // 是否是剩余时间，time1传参为毫秒
    isRemainingTime = false,
    // 截止文本
    deadlineText = '',
    // 根据配置返回转换值
    unit = '',
    // i18n
    $i18n = i18n
  } = params

  // 默认剩余时间
  let millisecondDiff = time1

  if (!isRemainingTime) {
    // 非剩余时间，计算两个时间毫秒差异
    const dayjsTime1 = dayjs(time1)
    // 如果没有就按当前时间
    const dayjsTime2 = time2 ? dayjs(time2) : dayjs()
    if (!dayjsTime1.isValid() || !dayjsTime2.isValid()) {
      // 时间解析错误
      console.error('[dynamic-cutoff-time] 时间解析错误')
      return {
        value: deadlineText,
        status: false
      }
    }

    // 时间毫秒差异
    millisecondDiff = dayjsTime1.diff(dayjsTime2)
  }

  if (millisecondDiff <= 0) {
    // 理论上第2个时间需要比第1个时间大，如果小的话，代表已截止
    return {
      value: deadlineText,
      status: false
    }
  }

  // 根据毫秒计算年月天时分秒返回
  const timeValue = parseTimeByMillisecond(millisecondDiff, unit)
  let timeValueToString = ''
  if (timeValue) {
    if (unit) {
      // 国际化配置根据动态切换
      timeValueToString = timeValue[unit] + $i18n.t(`time.${unit}`)
    } else {
      // 计算返回字符串值，年和月是动态的，大于0才返回
      Object.keys(timeValue).forEach(item => {
        if (
          (item === 'years' && timeValue.years > 0) ||
          (item === 'months' && (timeValue.months > 0 || timeValue.years > 0)) ||
          !['years', 'months'].includes(item)
        ) {
          timeValueToString += timeValue[item] + $i18n.t(`time.${item}`)
        }
      })
    }
  }
  return {
    value: timeValueToString || deadlineText,
    status: !!timeValueToString
  }
}

/**
 * 计算时间差，返回格式化对象
 * @param {string | number} time1 对比时间1 可被dayjs格式化
 * @param {string | number | null} time2 对比时间2 可被dayjs格式化 若与当前时间对比传空
 * @param {object} params { deadlineText: '', unit: '' }
 * @param {boolean} isRemainingTime 是否是剩余时间，time1传参为毫秒
 * @param {string} unit 根据配置返回转换值，比如配置days，只返回天，hours只返回毫秒转的小时数以此类推
 * @param params
 * @returns {{value: ({hours: number, seconds: number, months: number, minutes: number, days: number, years: number}|{}|null), status: boolean}|{status: boolean}}
 */
export const dynamicCutoffTimeDiff = (time1, time2 = '', params = {}) => {
  const {
    // 是否是剩余时间，time1传参为毫秒
    isRemainingTime = false,
    // 根据配置返回转换值
    unit = ''
  } = params

  // 默认剩余时间
  let millisecondDiff = time1

  if (!isRemainingTime) {
    // 非剩余时间，计算两个时间毫秒差异
    const dayjsTime1 = dayjs(time1)
    // 如果没有就按当前时间
    const dayjsTime2 = time2 ? dayjs(time2) : dayjs()
    if (!dayjsTime1.isValid() || !dayjsTime2.isValid()) {
      // 时间解析错误
      console.error('[dynamic-cutoff-time] 时间解析错误')
      return {
        status: false
      }
    }

    // 时间毫秒差异
    millisecondDiff = dayjsTime1.diff(dayjsTime2)
  }

  if (millisecondDiff <= 0) {
    // 理论上第2个时间需要比第1个时间小，如果大的话，代表已截止
    return {
      status: false
    }
  }

  // 根据毫秒计算年月天时分秒返回
  return {
    value: parseTimeByMillisecond(millisecondDiff, unit),
    status: true
  }
}

/**
 * 时间倒计时类
 * diffValue是计算后的值，一个对象{hours: number, seconds: number, months: number, minutes: number, days: number, years: number}
 * 监听isDeadline可以得出是否已截止
 */
 export class DynamicCutoffTimeClass {
  constructor (option = {}) {
    // 定时器
    this.timer = null
    // 对比时间1
    this.diffTime1 = ''
    // 对比时间2
    this.diffTime2 = ''
    // 是否是剩余时间
    this.isRemainingTime = false
    // 对比计算值
    this.diffValue = {}
    // 是否已截止
    this.isDeadline = false
    // 配置
    this.setOption(option)
  }

  /**
   * 设置配置
   * @param option
   */
  setOption (option = {}) {
    const {
      // 截止时间
      deadlineTime = '',
      // 对比的时间
      deadlineDiffTime = '',
      // 是否是剩余时间，deadlineTime传参为毫秒
      isRemainingTime = false,
      // 自动启动
      auto = true
    } = option
    this.diffTime1 = deadlineTime
    this.diffTime2 = deadlineDiffTime
    this.isRemainingTime = isRemainingTime
    // 重置数据
    this.clearTimer()
    if (auto) {
      // 启动定时器
      this.setTimer()
    }
  }

  /**
   * 设置定时器
   */
  setTimer () {
    // 计算结果
    const calculateResult = dynamicCutoffTimeDiff(
      this.diffTime1,
      this.diffTime2,
      { isRemainingTime: this.isRemainingTime }
    )

    if (calculateResult.status) {
      this.timer = setInterval(() => {
        // 执行减1秒
        if (this.isRemainingTime) {
          // 毫秒
          this.diffTime1 -= 1000
        } else {
          // 浅拷贝，会影响原对象
          dayjs(this.diffTime1).subtract(1, 'second')
        }

        const result = dynamicCutoffTimeDiff(
          this.diffTime1,
          this.diffTime2,
          { isRemainingTime: this.isRemainingTime }
        )
        this.diffValue = result.value || {}
        this.isDeadline = !result.status
        if (!result.status) {
          // 已截止
          clearInterval(this.timer)
        }
      }, 1000)
    } else {
      // 已截止
      this.isDeadline = true
      clearInterval(this.timer)
    }
  }

  /**
   * 清除定时器，组件销毁时一定要调用该方法清除定时器
   */
  clearTimer () {
    clearInterval(this.timer)
  }
}
