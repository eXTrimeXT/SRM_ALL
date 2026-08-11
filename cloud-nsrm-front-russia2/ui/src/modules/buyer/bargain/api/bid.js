/**
 * @description 采购商 - bid
 * @author donghf3
 */
import http from '@/utils/axios/http'

const formula = {
  // 查询公式列表 入参：[{ materialId, categoryId, orgOuId }]
  getMaterialFormulaRelateInfos: async payload =>
    http({
      url: '/api-base/bid/pricing-formula/getMaterialFormulaRelateInfos',
      method: 'POST',
      data: payload,
      loading: true
    })
}

export default {
  formula
}
