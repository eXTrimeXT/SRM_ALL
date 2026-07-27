/* 寻源 */

import http from '@/utils/axios/http'

/* 物料属性清单 */
export const attr = {
  listPage: async data =>
    http({
      url: '/api-base/base/material-item-attribute/listMaterialItemAttribute',
      method: 'POST',
      data
    }),
  deleteItem: async id =>
    http({
      url: '/api-base/base/material-item-attribute/deleteMaterialItemAttributeById',
      method: 'GET',
      params: { id }
    }),
  add: async data =>
    http({
      url: '/api-base/base/material-item-attribute/createMaterialItemAttribute',
      method: 'POST',
      data
    })
}

// 基材档案
export const bm = {
  queryBaseMaterialByPage: async data =>
    http({
      url: '/api-base/bid/base-material/queryBaseMaterialByPage',
      method: 'POST',
      data
    }),
  createBaseMaterial: async data =>
    http({
      url: '/api-base/bid/base-material/createBaseMaterial',
      method: 'POST',
      data
    }),
  saveBaseMaterialTemporary: async data =>
    http({
      url: '/api-base/bid/base-material/saveBaseMaterialTemporary',
      method: 'POST',
      data
    }),
  inActiveBaseMateria: async id =>
    http({
      url: '/api-base/bid/base-material/inActiveBaseMateria',
      method: 'GET',
      params: { id }
    }),
  deleteBaseMaterialById: async id =>
    http({
      url: '/api-base/bid/base-material/deleteBaseMaterialById',
      method: 'GET',
      params: { id }
    }),
  activeBaseMateria: async data =>
    http({
      url: '/api-base/bid/base-material/activeBaseMaterial',
      method: 'POST',
      data
    })
}

// 基材价格
export const bp = {
  listPage: async data =>
    http({
      url: '/api-base/bid/base-material-price/queryBaseMaterialPriceByPage',
      method: 'POST',
      data
    }),
  add: async data =>
    http({
      url: '/api-base/bid/base-material-price/createBaseMaterialPrice',
      method: 'POST',
      data
    }),
  save: async data =>
    http({
      url: '/api-base/bid/base-material-price/saveBaseMaterialPriceTemporary',
      method: 'POST',
      data
    }),
  active: async data =>
    http({
      url: '/api-base/bid/base-material-price/activeBaseMaterialPrice',
      method: 'POST',
      data
    }),
  deleteItem: async id =>
    http({
      url: '/api-base/bid/base-material-price/deleteBaseMaterialPriceById',
      method: 'GET',
      params: { id }
    }),
  inActive: async id =>
    http({
      url: '/api-base/bid/base-material-price/inActiveBaseMaterialPrice',
      method: 'GET',
      params: { id }
    }),
  drop: async id =>
    http({
      url: '/api-base/bid/base-material-price/dropBaseMaterialPrice',
      method: 'GET',
      params: { id }
    })
}

/* 获取招标物料列表 */
export const getRequireInfoByBidingId = async id =>
  http({
    url: `/api-bid/bidInitiating/biding/getRequireInfo/${id}`,
    method: 'GET',
    loading: true
  })

export const formula = {
  get: async headerId =>
    http({
      url: '/api-base/bid/pricing-formula/getPricingFormulaById',
      method: 'GET',
      params: { headerId }
    }),
  listPage: async data =>
    http({
      url: '/api-base/bid/pricing-formula/listPricingFormulaHeaderByPage',
      method: 'POST',
      data
    }),
  tempSave: async data =>
    http({
      url: '/api-base/bid/pricing-formula/tempSave',
      method: 'POST',
      data
    }),
  active: async id =>
    http({
      url: `/api-base/bid/pricing-formula/active/${id}`,
      method: 'POST',
      data: {}
    }),
  invalid: async id =>
    http({
      url: `/api-base/bid/pricing-formula/invalid/${id}`,
      method: 'POST',
      data: {}
    }),
  deleteItem: async headerId =>
    http({
      url: '/api-base/bid/pricing-formula/deletePricingFormulaById',
      method: 'GET',
      params: { headerId }
    }),
  // 查询公式列表 入参：[{ materialId, categoryId, orgOuId }]
  getMaterialFormulaRelateInfos: async payload =>
    http({
      url: '/api-base/bid/pricing-formula/getMaterialFormulaRelateInfos',
      method: 'POST',
      data: payload,
      loading: true
    })
}

// 报价模板
export const priceTemplateApi = {
  // 获取报价模板详情信息
  getDetail: id =>
    http({
      url: `/api-bid/buyer/quote-temp/detail/${id}`,
      method: 'GET',
      loading: true
    })
}

export const queryEssentialFactorByPage = async (data) =>
  http({
    url: '/api-base/price/essential-factor/queryEssentialFactorByPage',
    method: 'POST',
    data
  })

export const sourcingTemplate = {
  listPage: async data =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplat/findSourcingTemplatesWithoutTemplateData',
      method: 'POST',
      data
    }),
  findData: async id =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplate/findSourcingTemplateData',
      method: 'GET',
      params: { id }
    }),
  draft: async data =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplate/draftSourcingTemplate',
      method: 'POST',
      data
    }),
  valid: async ids =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplate/validSourcingTemplates',
      method: 'POST',
      params: { ids: ids.join() }
    }),
  invalid: async ids =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplate/invalidSourcingTemplates',
      method: 'POST',
      params: { ids: ids.join() }
    }),
  delete: async ids =>
    http({
      url: '/api-bid/bidInitiating/sourcingTemplate/deleteSourcingTemplates',
      method: 'DELETE',
      params: { ids: ids.join() }
    })
}

// 寻源和询价比公用接口 根据寻源模板ID快捷创建招投标或者询比价
export const generateSourceForm = async id =>
  http({
    url: '/api-bid/bidInitiating/sourcingTemplate/generateSourceForm',
    method: 'POST',
    params: { id }
  })

// 要素定义
export const elem = {
  listPage: async data =>
    http({
      url: '/api-base/bid/essential-factor/queryEssentialFactorByPage',
      method: 'POST',
      data
    }),
  add: async data =>
    http({
      url: '/api-base/bid/essential-factor/createEssentialFactor',
      method: 'POST',
      data
    }),
  save: async data =>
    http({
      url: '/api-base/bid/essential-factor/saveEssentialFactorTemporary',
      method: 'POST',
      data
    }),
  active: async data =>
    http({
      url: '/api-base/bid/essential-factor/activeEssentialFactor',
      method: 'POST',
      data
    }),
  deleteItem: async id =>
    http({
      url: '/api-base/bid/essential-factor/deleteEssentialFactorById',
      method: 'GET',
      params: { id }
    }),
  inActive: async id =>
    http({
      url: '/api-base/bid/essential-factor/inActiveEssentialFactor',
      method: 'GET',
      params: { id }
    }),
  queryEssential: async essentialFactorId =>
    http({
      url: '/api-base/bid/essential-factor/queryEssentialFactorById',
      method: 'GET',
      params: { essentialFactorId },
      loading: true
    })
}
