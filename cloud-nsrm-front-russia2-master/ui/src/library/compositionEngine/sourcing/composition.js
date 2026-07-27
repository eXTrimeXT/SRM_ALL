/**
 * @description 寻源模块公共方法，不同页面共用的都会放这里
 */
import { MessageBox, Message } from '@meicloud/element-ui'
import { parseTime } from '@/utils'
import { common } from '@/service/modules/utils'
import { FLOAT_FORMAT_MAGIC } from '@/config/sysConfig'
import { BUSINESS_TYPE_ENUM } from './enum'
import { messageConfig } from '@/utils/message'
import Big from 'big.js'
import i18n from '@/lang'

// 业务类型 list
export const BUSINESS_TYPE = Object.keys(BUSINESS_TYPE_ENUM)

/**
 * @description 校验业务类型是否合法
 * @author donghf3
 * @param type
 * @returns {boolean}
 */
export const validatorBusinessType = type => !!type && !!BUSINESS_TYPE_ENUM[type]

/**
 * @description 根据税率 和 未税单价 算出含税单价
 * @author donghf3
 * @param noTaxPrice 未税单价
 * @param tax 税率
 * @param round 保留小数位
 * @returns {string|*}
 */
export const bigCalcTaxPrice = (noTaxPrice, tax, round = FLOAT_FORMAT_MAGIC.DIGITS) => {
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

/**
 * @description 简易询价和招标，公式报价下，判断某一行数据是否满足查询公式列表的条件，符合条件返回行参数
 * @author donghf3
 * @param orgOuId 业务实体
 * @param noCodeItem 是否无料号寻源
 * @param categoryId 品类
 * @param itemId 物料
 * @param isFormula 是否是公式报价
 * @returns {{orgOuId: *, mapKey: string, valueKey: string, categoryId}|boolean|{orgOuId: *, mapKey: string, materialId, valueKey: string}}
 */
export const getFormulaValuePreconditions = ({ orgOuId, noCodeItem, categoryId, itemId }, isFormula) => {
  // 必须是公式报价
  if (!isFormula) {
    return false
  }

  // 业务实体必选
  if (!orgOuId) {
    return false
  }
  const param = { orgOuId }

  // 无料号寻源，需要选品类
  if (noCodeItem === 'Y' && categoryId) {
    return {
      ...param,
      categoryId,
      mapKey: 'categoryRelateMap',
      valueKey: categoryId.toString()
    }
  }

  // 非无料号寻源，需要选物料编码
  if ((noCodeItem === 'N' || !noCodeItem) && itemId) {
    return {
      ...param,
      materialId: itemId,
      mapKey: 'materialRelateMap',
      valueKey: itemId.toString()
    }
  }
  return false
}

/**
 * @description 时间格式化为y-m-d (准备废弃统一用dayjs)
 * @author donghf3
 * @param time {string} 需要格式化的时间
 * @returns {string|null|string}
 */
export const parseTimeYMD = time => {
  return time ? parseTime(time, '{y}-{m}-{d}') : ''
}

/**
 * @description 物料编码判断显示 无编码物料 - 显示为空
 * @author donghf3
 * @param targetNum 物料编码
 * @returns {string|*}
 */
export const targetNumReveal = targetNum => {
  return targetNum && targetNum.indexOf('VIRTUAL_') === 0 ? '' : targetNum
}

/**
 * @description 判断流程审批节点之前已生效的节点是否都已完成
 * @author donghf3
 * @param enabledList 已启用的节点
 * @param flagList 已完成的节点
 * @param nodeId 节点ID
 * @returns {boolean|boolean}
 */
export const scopePrepareWorkflow = (enabledList, flagList, nodeId) => {
  // 判断有没有流程审批节点，如果有，判断能否进行流程审批
  const index = enabledList.findIndex(item => item === nodeId)
  const afterNodes = enabledList.slice(0, index)
  // 判断流程之前的节点是否已完成
  return index >= 0 ? !afterNodes.find(item => !flagList.includes(item)) : false
}

/**
 * @description 查询是否开启审批流 暂时只能用列表查询接口
 * @author donghf3
 * @param modelId 流程模板ID
 * @returns {Promise<boolean>}
 */
export const getFlowByIdFromListPage = async modelId => {
  const response = await common(
    '/api-base/flow/processTemplent/listPage',
    {
      queryParams: { modelId },
      method: 'POST'
    }
  ).catch(() => { /* nothing */ })

  if (response && response.data && response.data.list && response.data.list.length === 1) {
    return response.data.list[0].enableFlag === 'Y'
  } else {
    return false
  }
}

/**
 * @description 评选表格合并行
 * @author donghf3
 * @param column 列配置
 * @param rowIndex 行号
 * @returns {{colspan: number, rowspan: number}}
 */
export const quoteSelectionTableSpanMethod = (column, rowIndex) => {
  if (column.property === 'priceComparison') {
    // 比价列合并
    if (rowIndex === 0) {
      return {
        rowspan: 10000,
        colspan: 1
      }
    } else {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
  }
}

/**
 * @description 评选表格表头样式，实现一个一级表头对应两列
 * @author donghf3
 * @param rowIndex 表行号
 * @returns {{display: string}}
 */
export const quoteSelectionTableHeaderCellStyle = rowIndex => {
  if (rowIndex === 1) {
    // 隐藏二级表头
    return {
      display: 'none'
    }
  }
}

/**
 * @description 根据业务类型获取api服务前缀
 * @author donghf3
 * @param businessType
 * @returns {string}
 */
export const getApiServerNameByBusinessType = businessType => {
  const obj = {
    BIDING: '/api-bid',
    BARGAIN: '/api-brg',
    INQUIRY: '/api-inq',
    COMPETITION: '/api-comp'
  }
  return obj[businessType] || ''
}

/**
 * @description 针对行报错信息进行警告提示
 * @author donghf3
 * @param index 行序号
 * @param message 消息
 */
export const indexWarningMessage = (index, message) => {
  Message({
    ...messageConfig,
    type: 'warning',
    message: i18n.t('bidMod.warningMessage', { index: (index + 1), message })
  })
}

/**
 * @description 判断对象列表列表中是否存在相同
 * @author donghf3
 * @param list
 * @param valueKey
 * @param tagKey
 */
export const judgeListRepeatValueWarnTag = (list, valueKey, tagKey) => {
  // 入参校验
  if (!list || !Array.isArray(list) || !valueKey || !tagKey) {
    return
  }

  const newList = list.concat()

  return newList.map((item, index) => {
    return {
      ...item,
      // 判断并标记是否存在相同的值
      [tagKey]: !!newList.find((warnItem, warnIndex) => {
        return item[valueKey] === warnItem[valueKey] && index !== warnIndex
      })
    }
  })
}

/**
 * @description 寻源模块。生成价格审批单统一方法
 * @param businessType 业务类型
 * @param apiUrl api地址
 * @param params 参数
 * @param router 路由实例
 * @returns {Promise<void>}
 */
export const createPricingApproval = async (businessType, apiUrl, params = {}, router) => {
  if (!businessType || !apiUrl) {
    return
  }

  const confirmSubmitResult = await MessageBox.confirm(i18n.t('competition.createPricingApproval'), {
    confirmButtonText: i18n.t('common.confirm'),
    cancelButtonText: i18n.t('common.cancel'),
    type: 'warning'
  }).catch(() => { /* nothing */ })

  if (confirmSubmitResult !== 'confirm') {
    return
  }

  const response = await common(
    apiUrl,
    {
      queryParams: params,
      method: 'POST'
    }
  ).catch(() => { /* nothing */ })

  if (!response || !response.data) {
    return
  }

  const confirmResult = await MessageBox.confirm(i18n.t('competition.createPricingApprovalAndJump'), {
    confirmButtonText: i18n.t('competition.jumpPricingApproval'),
    cancelButtonText: i18n.t('common.cancel'),
    type: 'warning'
  })

  if (confirmResult === 'confirm') {
    // 确认跳转
    router.push({
      name: 'priceApproval',
      params: {
        from: 'fromFun',
        formId: response.data.approvalHeaderId,
        formNo: response.data.approvalNo,
        funName: 'priceApproval',
        sourceType: businessType
      }
    })
  }
}

/**
 * 判断寻源单来源，是否手工创建
 * @param sourceFrom 来源字段，寻源模块都一样
 * @returns {boolean}
 */
export const souProjectSourceFromManual = sourceFrom => {
  if (!sourceFrom) {
    // 默认true
    return true
  }
  if (typeof sourceFrom === 'string') {
    return sourceFrom === 'MANUAL'
  }
  return false
}
