/**
 * 招投标API
 */

import http from '@/utils/axios/http'

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

export const modelQuote = {
  // 寻源需求行【模型报价】查询明细
  find: async lineId =>
    http({
      url: '/api-bid/bidInitiating/bidRequirementLineTemplatePrice/findDetailsByLineId',
      method: 'GET',
      params: { lineId }
    }),
  // 寻源需求行【模型报价】查询明细
  findByVendor: async lineId =>
    http({
      url: '/api-bid/techProposal/bidOrderLineTemplatePriceDetails/findDetailsByLineId',
      method: 'GET',
      params: { lineId }
    }),
  // 寻源需求行【模型报价】保存明细集-采购商
  saveByBuyer: async data =>
    http({
      url: '/api-bid/bidInitiating/bidRequirementLineTemplatePrice/saveDetails',
      method: 'POST',
      data
    }),
  // 采购商-评选查看报价模型横向对比
  generateTemplatePriceReport: async data =>
    http({
      url: '/api-bid/bidingResult/generateTemplatePriceReport',
      method: 'POST',
      data
    }),
  // 寻源需求行【模型报价】保存明细集-供应商
  saveByVendor: async data =>
    http({
      url: '/api-bid/techProposal/bidOrderLineTemplatePriceDetails/saveDetails',
      method: 'POST',
      data
    }),
  // 查询模型报价接口，采购商和供应商同一个
  getOrderModelPrices: async params =>
    http({
      url: '/api-bid/supplierCooperate/orderHead/getOrderModelPrices',
      method: 'GET',
      params,
      loading: true
    })
}

export const bidding = {
  expertListPage: async data =>
    http({
      url: '/api-bid/bidExpert/listPage',
      method: 'POST',
      data
    }),
  getRateByCode: async ({ fromCode, toCode }) =>
    http({
      url: '/api-bid/bidInitiating/biding/getRateByCode',
      method: 'GET',
      params: { fromCode, toCode }
    })
}

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

export const ouGroup = {
  listPage: async data =>
    http({
      url: '/api-base/base/base-ou-group/queryByPage',
      method: 'POST',
      data,
      loading: true
    }),
  create: async data =>
    http({
      url: '/api-base/base/base-ou-group/create',
      method: 'POST',
      data,
      loading: true
    }),
  updateById: async data =>
    http({
      url: '/api-base/base/base-ou-group/updateById',
      method: 'POST',
      data,
      loading: true
    }),
  queryById: async id =>
    http({
      url: '/api-base/base/base-ou-group/queryById',
      method: 'GET',
      params: { id },
      loading: true
    }),
  active: async groupId =>
    http({
      url: '/api-base/base/base-ou-group/activeGroup',
      method: 'GET',
      params: { groupId },
      loading: true
    }),
  invalid: async groupId =>
    http({
      url: '/api-base/base/base-ou-group/invalidGroup',
      method: 'GET',
      params: { groupId },
      loading: true
    }),
  abandon: async groupId =>
    http({
      url: '/api-base/base/base-ou-group/abandonGroup',
      method: 'GET',
      params: { groupId },
      loading: true
    })
}

export const verifyMaterialEditable = async data =>
  http({
    url: '/api-base/purchase/purchaseCategory/queryCategoryMaxCodeByMaterialIds',
    method: 'POST',
    data,
    loading: true
  })

/* 获取招标物料列表 */
export const getRequireInfoByBidingId = async id =>
  http({
    url: `/api-bid/bidInitiating/biding/getRequireInfo/${id}`,
    method: 'GET',
    loading: true
  })

// 查询供应商公式报价
export const getQuoteFormulaPrices = async params =>
  http({
    url: '/api-bid/supplierCooperate/orderHead/getQuoteFormulaPrices',
    method: 'GET',
    params,
    loading: true
  })

// 报价
export const quote = {
  // 批量查询阶梯报价
  batchGetQuoteLadderPrices: data =>
    http({
      url: '/api-bid/bidInitiating/biding/batchGetQuoteLadderPrices',
      method: 'POST',
      data,
      loading: true
    })
}

// 报价模板报价应用
export const templatePrice = {
  // 获取 / 保存 报价模板预览数据
  getOrSaveTempDataUrl: '/api-bid/supplierCooperate/orderHead/order/temp'
}
