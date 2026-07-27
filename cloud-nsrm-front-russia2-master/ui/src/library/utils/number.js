import Big from 'big.js'
import i18n from '@/lang'

/**
 * 数字分隔格式化处理
 * @param {Number|String} source 需要进行处理的数字
 * @param {Number} [length=3] 需要分隔的位数, 默认 3 位
 * @returns {String}
 */
export const comma = (source, length = 3) => {
  if (typeof source === 'undefined' || source === null) {
    return 0
  }

  source = String(source).split('.')
  source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{' + length + '})+$)', 'ig'), '$1,')
  return source.join('.')
}

/**
 * 百分比转数字
 * @param {Number|String} percent 百分比值
 * @param {Number} floatNumber 保留小数位
 * @returns {Number}
 */
export const percentToNumber = (percent, floatNumber = 2) => {
  if (typeof percent === 'string' && percent.indexOf('%') > -1) {
    percent = Number(percent.replace('%', ''))
  }
  percent = new Big(percent)

  return percent.div(100).toFixed(floatNumber).valueOf()
}

/**
 * 数字转百分比
 * @param {Number|String} number 数字
 * @param {Number} floatNumber 保留小数位
 * @returns {Number}
 */
export const numberToPercent = (number, floatNumber = 2) => {
  if (number === 'undefined' || number === null) return

  let result = 0
  number = new Big(number || 0)
  result = number.times(100).toFixed(floatNumber).valueOf()
  return `${result}%`
}

/**
 * 数字金额大写
 * @param {Number|String} n 数字
 * @returns {Number}
 */
export const numericUppercase = (n) => {
  if (!/^(0|[1-9]\d*)(\.\d+)?$/.test(n)) {
    return i18n.t('common.illegalData') // 判断数据是否大于0
  }

  var unit = '仟佰拾亿仟佰拾万仟佰拾圆角分'
  var str = ''
  n += '00'

  var indexpoint = n.indexOf('.') // 如果是小数，截取小数点前面的位数

  if (indexpoint >= 0) {
    n = n.substring(0, indexpoint) + n.substr(indexpoint + 1, 2) // 若为小数，截取需要使用的unit单位
  }

  unit = unit.substr(unit.length - n.length) // 若为整数，截取需要使用的unit单位
  for (var i = 0; i < n.length; i++) {
    str += '零壹贰叁肆伍陆柒捌玖'.charAt(n.charAt(i)) + unit.charAt(i) // 遍历转化为大写的数字
  }

  return str
    .replace(/零(仟|佰|拾|角)/g, '零')
    .replace(/(零)+/g, '零')
    .replace(/零(万|亿|圆)/g, '$1')
    .replace(/(亿)万|壹(拾)/g, '$1$2')
    .replace(/^圆零?|零分/g, '')
    .replace(/圆$/g, '圆整') // 替换掉数字里面的零字符，得到结果
}
