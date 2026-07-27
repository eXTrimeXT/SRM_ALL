/**
 * 全局composition
 */
import Big from 'big.js'

/**
 * @description 传入一个数字，如4，会返回下拉框配置[{ id: 'roundNum-1', label: 1, value: 1 }...]
 * @author donghf3
 * @param num
 * @returns {{id: string, label: number, value: number}[]|*[]}
 */
export const maxNumberOption = (num) => {
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
export const bigPriceRound = (price, round = 2) => {
  try {
    const bigPrice = new Big(price)
    // Big.roundDown 向下截取
    return bigPrice.round(round, Big.roundDown).toString()
  } catch (e) {
    return price
  }
}

/**
 * @description 根据配置的入参，格式化daterange类型的参数
 * @author donghf3
 * @param payload 参数对象
 * @param propList 配置列表 => [{ prop: 'creationDate', fromProp: 'creationDateFrom', toProp: 'creationDateTo' }]
 */
export const daterangePayloadFormat = (payload, propList) => {
  if (!payload || typeof payload !== 'object') {
    return
  }

  let newPayload = JSON.parse(JSON.stringify(payload))
  propList.forEach(item => {
    // 判断条件是否存在
    const propData = newPayload[item.prop]
    if (propData && Array.isArray(propData) && propData.length === 2) {
      newPayload = {
        ...newPayload,
        [item.fromProp]: propData[0],
        [item.toProp]: propData[1]
      }
      // 删除原属性
      delete newPayload[item.prop]
    }
  })
  return newPayload
}
