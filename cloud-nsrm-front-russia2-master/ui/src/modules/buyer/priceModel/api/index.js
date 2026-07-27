import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const basePrice = {
  // 基价维护分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-price/base-price/listPage'),
      method: 'POST',
      data
    }),
    // 基价维护更新
  modify: async data =>
    http({
      url: getUrl('/api-price/base-price/modify'),
      method: 'POST',
      data
    }),
  // 基价维护生效
  takeEffect: async basePriceId =>
    http({
      url: getUrl('/api-price/base-price/takeEffect'),
      method: 'GET',
      params: { basePriceId }
    }),
  // 基价维护失效
  failure: async basePriceId =>
    http({
      url: getUrl('/api-price/base-price/failure'),
      method: 'GET',
      params: { basePriceId }
    }),
    // 根据成本要素属性值组合查询基价
  queryBy: async data =>
    http({
      url: getUrl('/api-price/base-price/queryBy'),
      method: 'POST',
      data
    })
}

export const costElement = {
  // 成本要素分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-price/cost-element/listPage'),
      method: 'POST',
      data
    }),
  // 成本要素分页查询
  listCostElement: async data =>
    http({
      url: getUrl('/api-price/cost-element/listCostElement'),
      method: 'POST',
      data
    }),
  // 获取成本要素详情
  get: async costElementId =>
    http({
      url: getUrl('/api-price/cost-element/get'),
      method: 'GET',
      params: { costElementId },
      loading: true
    }),
  // 暂存
  tempSave: async data =>
    http({
      url: getUrl('/api-price/cost-element/tempSave'),
      method: 'POST',
      data,
      loading: true
    }),
  // 提交
  submit: async data =>
    http({
      url: getUrl('/api-price/cost-element/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  // 成本要素删除
  delete: async costElementId =>
    http({
      url: getUrl(`/api-price/cost-element/delete/${costElementId}`),
      method: 'POST',
      data: {},
      loading: true
    }),
  // 成本要素生效
  takeEffect: async costElementId =>
    http({
      url: getUrl('/api-price/cost-element/takeEffect'),
      method: 'GET',
      params: { costElementId },
      loading: true
    }),
  // 成本要素失效
  failure: async costElementId =>
    http({
      url: getUrl('/api-price/cost-element/failure'),
      method: 'GET',
      params: { costElementId },
      loading: true
    }),
  // 创建新版本
  createNewVersion: async costElementId =>
    http({
      url: getUrl(`/api-price/cost-element/createNewVersion/${costElementId}`),
      method: 'POST',
      data: {},
      loading: true
    }),
  // 复制
  copy: async costElementId =>
    http({
      url: getUrl(`/api-price/cost-element/copyCostElement/${costElementId}`),
      method: 'POST',
      data: {},
      loading: true
    })
}

export const elementDefinition = {
  listPage: async data =>
    http({
      url: getUrl('/api-base/price/essential-factor/queryEssentialFactorByPage'),
      method: 'POST',
      data
    }),
  add: async data =>
    http({
      url: getUrl('/api-base/price/essential-factor/createEssentialFactor'),
      method: 'POST',
      data
    }),
  save: async data =>
    http({
      url: getUrl(
        '/api-base/price/essential-factor/saveEssentialFactorTemporary'
      ),
      method: 'POST',
      data
    }),
  active: async data =>
    http({
      url: getUrl('/api-base/price/essential-factor/activeEssentialFactor'),
      method: 'POST',
      data
    }),
  deleteItem: async id =>
    http({
      url: getUrl('/api-base/price/essential-factor/deleteEssentialFactorById'),
      method: 'GET',
      params: { id }
    }),
  inActive: async id =>
    http({
      url: getUrl('/api-base/price/essential-factor/inActiveEssentialFactor'),
      method: 'GET',
      params: { id }
    }),
  queryEssential: async essentialFactorId =>
    http({
      url: getUrl('/api-base/price/essential-factor/queryEssentialFactorById'),
      method: 'GET',
      params: { essentialFactorId },
      loading: true
    })
}

export const estimatingPrice = {
  // 价格估算分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-price/estimate-head/listPage'),
      method: 'POST',
      data
    }),
  // 价格估算暂存
  tempSave: async data =>
    http({
      url: getUrl('/api-price/estimate-head/tempSave'),
      method: 'POST',
      data,
      loading: true
    }),
  // 价格估算测试
  testEstimate: async data =>
    http({
      url: getUrl('/api-price/estimate-head/testEstimate'),
      method: 'POST',
      data,
      loading: true
    }),
  // 价格估算提交
  submit: async data =>
    http({
      url: getUrl('/api-price/estimate-head/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  // 价格估算生效
  valid: async estimateHeadId =>
    http({
      url: getUrl(`/api-price/estimate-head/valid/${estimateHeadId}`),
      method: 'POST',
      data: {},
      loading: true
    }),
  // 价格估算失效
  invalid: async estimateHeadId =>
    http({
      url: getUrl(`/api-price/estimate-head/invalid/${estimateHeadId}`),
      method: 'POST',
      data: {},
      loading: true
    }),
  // 获取价格估算详情
  get: async estimateHeadId =>
    http({
      url: getUrl('/api-price/estimate-head/get'),
      method: 'GET',
      params: { estimateHeadId },
      loading: true
    }),
  // 价格估算删除
  delete: async estimateHeadId =>
    http({
      url: getUrl('/api-price/estimate-head/delete'),
      method: 'GET',
      params: { estimateHeadId },
      loading: true
    }),
  // 价格估算获取详情 根据核价模板ID查找详情
  attrGet: async params =>
    http({
      url: getUrl('/api-price/estimate-attr-head/get'),
      method: 'GET',
      params: params,
      loading: true
    }),
  // 根据币种和汇率类型批量查询汇率
  getPriceTaxList: async data =>
    http({
      url: '/api-base/purchase/purchaseExchangeRate/getPriceTaxList',
      method: 'POST',
      data,
      loading: true
    })
}

export const formula = {
  get: async headerId =>
    http({
      url: getUrl('/api-base/price/pricing-formula/getPricingFormulaById'),
      method: 'GET',
      params: { headerId }
    }),
  listPage: async data =>
    http({
      url: getUrl(
        '/api-base/price/pricing-formula/listPricingFormulaHeaderByPage'
      ),
      method: 'POST',
      data
    }),
  add: async data =>
    http({
      url: getUrl('/api-base/price/pricing-formula/createPricingFormula'),
      method: 'POST',
      data
    }),
  save: async data =>
    http({
      url: getUrl('/api-base/price/pricing-formula/savePricingFormulaTemporary'),
      method: 'POST',
      data
    }),
  active: async data =>
    http({
      url: getUrl('/api-base/price/pricing-formula/activePricingFormula'),
      method: 'POST',
      data
    }),
  deleteItem: async headerId =>
    http({
      url: getUrl('/api-base/price/pricing-formula/deletePricingFormulaById'),
      method: 'GET',
      params: { headerId }
    }),
  inActive: async id =>
    http({
      url: getUrl('/api-base/price/pricing-formula/inActivePricingFormula'),
      method: 'GET',
      params: { id }
    }),

  //     根据报价行的公式id返回要素，公式因子
  // /api-base/bid/pricing-formula/getFactorByFormulaId?formulaId
  getFactorByFormulaId: async formulaId =>
    http({
      url: getUrl('/api-base/price/pricing-formula/getFactorByFormulaId'),
      method: 'GET',
      params: { formulaId }
    }),
  // 根据报价行的物料id查询关联的物料主属性，物料关键主属性
  // /api-base/base/material-item-attribute/getMaterialAttributeRelate
  getMaterialAttributeRelate: async ({ materialId, formulaId, bidingId, sourcingType }) =>
    http({
      url: getUrl(
        '/api-base/base/material-item-attribute/getMaterialAttributeRelate'
      ),
      method: 'GET',
      params: { materialId, formulaId, bidingId, sourcingType }
    }),

  // 根据报价行ID查询 明细
  findDetailsByLineId: async lineId =>
    http({
      url: getUrl(
        '/api-bid/techProposal/bidOrderLineFormulaPriceDetails/findDetailsByLineId'
      ),
      method: 'GET',
      params: { lineId }
    }),

  // 保存 公式报价 明细
  // /api-base/techProposal/bidOrderLineFormulaPriceDetails/saveDetails
  saveDetails: async data =>
    http({
      url: getUrl(
        '/api-bid/techProposal/bidOrderLineFormulaPriceDetails/saveDetails'
      ),
      method: 'POST',
      data
    })
}

export const material = {
  queryMaterialByPage: async data =>
    http({
      url: getUrl('/api-base/price/material/queryMaterialByPage'),
      method: 'POST',
      data
    }),
  createMaterial: async data =>
    http({
      url: getUrl('/api-base/price/material/createMaterial'),
      method: 'POST',
      data
    }),
  saveMaterialTemporary: async data =>
    http({
      url: getUrl('/api-base/price/material/saveMaterialTemporary'),
      method: 'POST',
      data
    }),
  inActiveMateria: async id =>
    http({
      url: getUrl('/api-base/price/material/inActiveMateria'),
      method: 'GET',
      params: { id }
    }),
  deleteMaterialById: async id =>
    http({
      url: getUrl('/api-base/price/material/deleteMaterialById'),
      method: 'GET',
      params: { id }
    }),
  activeMateria: async data =>
    http({
      url: getUrl('/api-base/price/material/activeMaterial'),
      method: 'POST',
      data
    })
}
export const materialPrice = {
  listPage: async data =>
    http({
      url: getUrl(
        '/api-base/price/material-price/queryBaseMaterialPriceByPage'
      ),
      method: 'POST',
      data
    }),
  add: async data =>
    http({
      url: getUrl('/api-base/price/material-price/createBaseMaterialPrice'),
      method: 'POST',
      data
    }),
  save: async data =>
    http({
      url: getUrl(
        '/api-base/price/material-price/saveBaseMaterialPriceTemporary'
      ),
      method: 'POST',
      data
    }),
  active: async data =>
    http({
      url: getUrl('/api-base/price/material-price/activeBaseMaterialPrice'),
      method: 'POST',
      data
    }),
  deleteItem: async id =>
    http({
      url: getUrl(
        '/api-base/price/material-price/deleteBaseMaterialPriceById'
      ),
      method: 'GET',
      params: { id }
    }),
  inActive: async id =>
    http({
      url: getUrl(
        '/api-base/price/material-price/inActiveBaseMaterialPrice'
      ),
      method: 'GET',
      params: { id }
    }),
  drop: async id =>
    http({
      url: getUrl('/api-base/price/material-price/dropBaseMaterialPrice'),
      method: 'GET',
      params: { id }
    })
}
export const priceModel = {
  // 核价模型分页查询
  listPage: async data =>
    http({
      url: getUrl('/api-price/model-head/listPage'),
      method: 'POST',
      data
    }),
  // 用于在估价单中查询核价模型
  listPageForEstimate: async data =>
    http({
      url: getUrl('/api-price/model-head/listPageForEstimate'),
      method: 'POST',
      data
    }),
  // 核价模型暂存
  tempSave: async data =>
    http({
      url: getUrl('/api-price/model-head/tempSave'),
      method: 'POST',
      data,
      loading: true
    }),
  // 核价模型提交(生效)
  submit: async data =>
    http({
      url: getUrl('/api-price/model-head/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取核价模型详情
  get: async priceModelHeadId =>
    http({
      url: getUrl('/api-price/model-head/get'),
      method: 'GET',
      params: { priceModelHeadId },
      loading: true
    }),
  // 核价模型删除
  delete: async priceModelHeadId =>
    http({
      url: getUrl('/api-price/model-head/delete'),
      method: 'GET',
      params: { priceModelHeadId },
      loading: true
    }),
  // 核价模型生效
  takeEffect: async priceModelHeadId =>
    http({
      url: getUrl('/api-price/model-head/takeEffect'),
      method: 'GET',
      params: { priceModelHeadId },
      loading: true
    }),
  // 核价模型失效
  failure: async priceModelHeadId =>
    http({
      url: getUrl('/api-price/model-head/failure'),
      method: 'GET',
      params: { priceModelHeadId },
      loading: true
    })
}
