import { isNull } from '@/utils'
import {
  isEmpty,
  isFloat,
  isInteger,
  toInteger,
  toNumber as toNumberXE,
  commafy
} from 'xe-utils'
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'

// 默认的指令触发时机 bind blur
const BINDING_VALUE_HANDLE = ['bind', 'blur']

/**
 * @description 判断数值是否超过最小值最大值，并返回处理后的值
 * @author donghf3
 * @param max 最大
 * @param min 最小
 * @param value 值
 * @returns {number|*}
 */
const judgeNumberMaxAndMin = (max, min, value) => {
  // 最小
  if (!isNull(min) && value < toNumberXE(min)) {
    return toNumberXE(min)
  }
  // 最大
  if (!isNull(max) && value > toNumberXE(max)) {
    return toNumberXE(max)
  }
  return value
}

/**
 * @description 处理元素类型为输入框，并返回节点和是否启用
 * @author donghf3
 * @param el el
 * @returns {{unavailable: *, ele: *}}
 */
const judgeNodeAttributeUnavailable = el => {
  const ele = el.tagName.toUpperCase() === 'INPUT' ? el : el.querySelector('input')

  return {
    ele,
    // 只读或禁用框不处理
    unavailable: ele.getAttribute('readonly') || ele.getAttribute('disabled')
  }
}

/**
 * @description 处理小数位 并返回结果
 * @author donghf3
 * @param value value
 * @param digits digits
 * @param round round
 * @returns {number|*}
 */
const handleDigits = (value, digits, round) => {
  // 默认小数位全局配置
  digits = digits || FLOAT_FORMAT_MAGIC.DIGITS

  if (isFloat(value) && isInteger(digits) && digits > 0) {
    // 是浮点数 && 存在小数位限制
    value = toNumberXE(commafy(value, {
      digits,
      round,
      spaceNumber: -1
    }))
  }

  return value
}

/**
 * @description 处理数字类型
 * @author donghf3
 * @param value value
 * @param negative negative
 * @param zero zero
 * @param max max
 * @param min min
 * @returns {*}
 */
const handleNumeric = (value, negative, zero, max, min) => {
  // 默认数字都有最大值全局配置
  max = max || FLOAT_FORMAT_MAGIC.NUMBER
  // 默认数字都有最小值全局配置
  min = min || FLOAT_FORMAT_MAGIC.NEGATIVE

  // 处理数字负数配置
  if (!negative && value < 0) {
    // 不支持负数 且小于0 转正数
    value = value * -1
  }

  // 最大值最小值处理
  value = judgeNumberMaxAndMin(max, min, value)

  // 数值处理能否为0
  if (!zero && toNumberXE(value) === 0) {
    // 不能为0 且 转化后值是0 转空字符串
    value = ''
  }
  return value
}

/**
 * @description 处理数值千分位
 * @author donghf3
 * @param value value
 * @param spaceNumber spaceNumber
 * @param separator separator
 * @param digits digits
 * @param round round
 * @param negative negative
 * @param max max
 * @param min min
 * @returns {string}
 */
const handleThousandth = (value, spaceNumber, separator, digits, round, negative, max, min) => {
  // 默认数字都有最大值全局配置
  max = max || FLOAT_FORMAT_MAGIC.NUMBER
  // 默认数字都有最小值全局配置
  min = min || FLOAT_FORMAT_MAGIC.NEGATIVE
  // 默认小数位全局配置
  digits = digits || FLOAT_FORMAT_MAGIC.DIGITS

  let options = {
    spaceNumber,
    separator
  }

  // 如不配置小数位数，则不支持小数
  if (isInteger(digits) && digits > 0) {
    options = {
      ...options,
      digits,
      round
    }
  }
  // 先移除分位号
  value = value.toString().replace(separator, '')
  // 再转数字
  value = toNumberXE(value)
  // 处理数字负数配置
  if (!negative && value < 0) {
    // 不支持负数 且小于0 转正数
    value = value * -1
  }

  // 最大值最小值处理
  value = judgeNumberMaxAndMin(max, min, value)
  // 此时输出string
  value = commafy(value, options)

  return value
}

/**
 * @description 处理过滤字符串字符
 * @author donghf3
 * @param value value
 * @param filterOptions filterOptions
 * @returns {*}
 */
const handleFilterString = (value, filterOptions) => {
  // 配置列表中的字符不允许输入 目前暂只能以最小颗粒度过滤，待优化
  if (typeof value === 'string' && filterOptions.length) {
    value = value.replace(new RegExp(`[${filterOptions.join('')}]`, 'g'), '')
  }
  return value
}

/**
 * @description [handleInput 格式化处理输入框内容] 部分代码逻辑顺序很重要，注意不要颠倒
 * @description 支持配置多个触发时机判断，默认BINDING_VALUE_HANDLE ['bind', 'focus', 'blur'] [初始化, 获得焦点, 失去焦点]
 * @description 支持的类型 ['number', 'integer', 'float', 'thousandth', 'filterZhCn', 'letterAndNumber', 'filterString'] [数字, 整数, 浮点数, 数值分位, 过滤中文, 只允许数字字母, 过滤字符串字符]
 * @description 具体配置请查看 解构配置对象 binding.value
 * @author donghf3
 * @param ele {DOM} 当前指令操作的dom对象
 * @param binding {Object} 当前指令的传参 { type, negative, digits, round, spaceNumber, separator, zero, emit, data, handle }
 * @param vnode {DOM} 当前指令渲染的虚拟节点
 * @param event {'事件类型' | null} 事件类型
 */
function handleInput (ele, binding, vnode, event = null) {
  // 配置为空则不处理
  if (!binding.value || isEmpty(binding.value)) {
    return
  }

  // 解构配置对象
  let {
    // 配置类型 默认number
    type = 'number',
    // 负数 默认支持 ['number', 'integer', 'float', 'thousandth']
    negative = true,
    // 小数点位数，默认不限制，如配置thousandth类型会自动补0 ['number', 'float', 'thousandth']
    digits,
    // 小数位是否四舍五入 需配置digits才生效 ['number', 'float', 'thousandth']
    round = true,
    // 数值千分位分割位数 默认3 ['thousandth']
    spaceNumber = 3,
    // 数值千分位分割分隔符，默认半角逗号 ['thousandth']
    separator = ',',
    // 是否支持转化为0，默认支持，不支持就返回空字符串
    zero = true,
    // 配置的emit事件
    emit,
    // emit事件data
    data,
    // 过滤字符列表 正则中使用的特殊字符需要双重的转义符 如 '\\[' ['filterString’]
    filterOptions = []
  } = binding.value || {}

  // 解构元素属性
  const {
    // 最小值
    min,
    // 最大值
    max
  } = vnode.data.attrs || {}

  // 输入框值
  let value = ele.value

  if (isNull(value)) {
    // 空值
    value = ''
  } else {
    // 非空值
    switch (type) {
      case 'number':
        // 数字
        value = toNumberXE(value)
        break

      case 'integer':
        // 整数
        value = toInteger(value)
        break

      case 'float':
        // 浮点数
        value = toNumberXE(value)
        break

      case 'thousandth': {
        // 数值千分位
        value = handleThousandth(value, spaceNumber, separator, digits, round, negative, max, min)
        break
      }

      case 'filterZhCn':
        // 过滤中文及中文符号
        // eslint-disable-next-line no-control-regex
        value = value.replace(/[^\x00-\xff]/g, '')
        break

      case 'letterAndNumber':
        // 只能输入字母和数字
        value = value.replace(/[\W]/g, '')
        break

      case 'filterString':
        // 过滤字符串字符
        value = handleFilterString(value, filterOptions)
        break
    }

    // 处理小数位
    if (['number', 'float'].includes(type)) {
      value = handleDigits(value, digits, round)
    }

    // 处理数字类型
    if (['number', 'integer', 'float'].includes(type)) {
      value = handleNumeric(value, negative, zero, max, min)
    }
  }

  ele.value = value
  vnode.componentInstance.$emit('input', value)

  // emit事件
  if (event && emit) {
    vnode.componentInstance.$emit(emit, data)
  }
}

export default {
  // 初始化设置
  bind: (el, binding, vnode) => {
    const { ele, unavailable } = judgeNodeAttributeUnavailable(el)
    if (unavailable) {
      return
    }

    const { handle = BINDING_VALUE_HANDLE } = binding.value || {}

    vnode.context.$nextTick(() => {
      if (handle.includes('bind')) {
        handleInput(ele, binding, vnode)
      }
    })
  },

  // 被绑定元素插入父节点时
  inserted: (el, binding, vnode) => {
    const { ele, unavailable } = judgeNodeAttributeUnavailable(el)
    if (unavailable) {
      return
    }

    const { handle = BINDING_VALUE_HANDLE } = binding.value || {}

    // 获取焦点事件 获得焦点暂不处理
    ele.onfocus = function () {
      if (handle.includes('focus')) {
        handleInput(ele, binding, vnode, 'onfocus')
      }
    }

    // 失去焦点事件
    ele.onblur = function () {
      vnode.context.$nextTick(() => {
        if (handle.includes('blur')) {
          handleInput(ele, binding, vnode, 'onblur')
        }
      })
    }
  }
}
