/**
 * 寻源模块工具方法
 */
import Big from 'big.js'
// @ts-ignore
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'

/**
 * @description 传入一个数字，如4，会返回下拉框配置[{ id: 'roundNum-1', label: 1, value: 1 }...]
 * @author donghf3
 * @param num
 * @returns {{id: string, label: number, value: number}[]|*[]}
 */
const maxNumberOption = (num: number) => {
  // 默认1
  const round = num || 1
  if (round && !Number.isNaN(Number(round))) {
    return new Array(round).fill(round).map((item, index) => {
      return {
        id: `roundNum-${index}`,
        label: item - index,
        value: item - index
      }
    }).reverse()
  }
  return []
}

/**
 * @description 保留小数位 向下截取 非四舍五入
 * @author donghf3
 * @param price 价格
 * @param round 小数位
 * @returns {string|*}
 */
const bigPriceRound = (price: number | string, round = 2) => {
  try {
    const bigPrice = new Big(price)
    // Big.roundDown 向下截取
    return bigPrice.round(round, Big.roundDown).toString()
  } catch (e) {
    return price
  }
}

/**
 * @description 根据税率 和 未税单价 算出含税单价
 * @author donghf3
 * @param noTaxPrice 未税单价
 * @param tax 税率
 * @param round 保留小数位
 * @returns {string|*}
 */
const bigCalcTaxPrice = (noTaxPrice: number | string, tax: number | string, round = FLOAT_FORMAT_MAGIC.DIGITS) => {
  if (!tax) {
    return Big(noTaxPrice).round(round).toString()
  }
  try {
    const [bigTaxPrice, bigTax] = [Big(noTaxPrice), Big(tax)]
    // 税率转小数 加 1 乘 含税单价 向下取整小数
    return bigTax.div(100).plus(1).times(bigTaxPrice).round(round).toString()
  } catch (e) {
    return noTaxPrice
  }
}

export {
  maxNumberOption,
  bigPriceRound,
  bigCalcTaxPrice
}
